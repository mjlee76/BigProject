from typing import Union, List
from fastapi import FastAPI, File, UploadFile, HTTPException, Body, Depends
from pydantic import BaseModel
from langchain_chroma import Chroma
from langchain_openai import OpenAIEmbeddings

import json
import shutil
import os
import requests
import xml.etree.ElementTree as ET
import pandas as pd
import datetime
import basic_module as bm
import nsfw_detection as nd
import aiofiles
import asyncio
import logging

from basic_module import TextClassifier
from basic_module import ChangeText
from basic_module import MakeReport
from basic_module import LoadDocumentFile
from spam_detect import SpamDetector

# POST: to create data. GET: to read data. PUT: to update data. DELETE: to delete data.
app = FastAPI()

@app.get("/")
def read_root():
    return {"Hello": "World"}

# Setting environment
@app.on_event("startup")
async def startup_event():
    global api_key, llm
    path = "./api_key.txt"
    api_key = await bm.load_api_key(path)
    os.environ["OPENAI_API_KEY"] = api_key
    llm = await bm.selecting_model(api_key)
    
model_path = "./20250204_roberta 파인튜닝"
file_path = "./특이민원보고서_공직자응대매뉴얼.pdf"
UPLOAD_DIR = "./uploaded_images"
FILE_PATH = "./uploaded_documents"
os.makedirs(UPLOAD_DIR, exist_ok=True)

load_directory = "./nsfw_model"
image_model, processor, image_classifier = nd.load_model(load_directory)
spam_detector = SpamDetector()
classifier = TextClassifier()
changetexter = ChangeText()
report = MakeReport()

docu_loader = LoadDocumentFile()
logger = logging.getLogger("my_logger")

#게시글 작성자 정보
class UserInfo(BaseModel):
    user_id : str
    user_name: str
    gender : str
    role : str
    birth_date : str
    phone : str
    address : str
    email: str
    create_date : str
    count : int

# 게시글 정보
class PostBody(BaseModel):
    title: str
    content: str
    user: UserInfo

class Category(BaseModel):
    title: str
    content: str

#DB로 넘길 report 정보
class ReportBody(BaseModel):
    category : Category
    post_origin_data : str
    report_path : str
    create_date : str
    
class CombinedModel(BaseModel):
    post_data: PostBody
    report_req: ReportBody

#spam_detect용 class 생성성
class QuestionApiDTO(BaseModel):
    id: int
    title: str
    content: str

class PostData(BaseModel):
    title: str
    content: str

class QuestionData(BaseModel):
    question: List[QuestionApiDTO]

class SpamQuestionRequest(BaseModel):
    post_data: PostData
    question_data: QuestionData

#민원 악성탐지 및 순화
@app.post("/filtered_module")
async def update_item(data: CombinedModel):
    try:
        post_data = data.post_data
        report_req = data.report_req

        title = post_data.title
        content = post_data.content
        post_origin_data = {"제목": title, "내용": content} # 원문데이터 저장용
        report_req.post_origin_data = post_origin_data

        # 분류
        title_label = classifier.classify_text(title)
        content_label = classifier.classify_text(content)
        await changetexter.init()
        result = {}

        if title_label != '정상' or content_label != '정상':
            if title_label != '정상' and content_label == '정상':
                title_changed = await changetexter.change_text(title)
                post_data.title =  title_changed
                report_req.category.title = title_label

                # 팝업 날릴거
                result["제목"] = {"text": f"{title_changed}","경고문": f"{title_label} 감지"}
                result["내용"] = {"text": content}

            elif title_label == '정상' and content_label != '정상':
                content_changed = await changetexter.change_text(content)
                post_data.content =  content_changed
                report_req.category.content = content_label

                result["제목"] = {"text": title}
                result["내용"] = {"text": f"{content_changed}","경고문": f"{content_label} 감지"}
            else:
                title_changed = await changetexter.change_text(title)
                post_data.title =  title_changed
                content_changed = await changetexter.change_text(content)
                post_data.content =  content_changed
                report_req.category.title = title_label
                report_req.category.content = content_label
                result["제목"] = {"text": f"{title_changed}","경고문": f"{title_label} 감지"}
                result["내용"] = {"text": f"{content_changed}","경고문": f"{content_label} 감지"}
            
            # 보고서 생성
            return {
                "valid": True,
                "message": "악성 데이터 수신 및 처리 완료",
                "post_data": post_data.model_dump(),
                "report_req": report_req.model_dump()
            }

        else:
            report_req.category.title = title_label
            report_req.category.content = content_label
            return    {
                "valid": True,
                "message": "원문 데이터 수신 및 처리 완료",
                "post_data": post_data.model_dump(),
                "report_req": report_req.model_dump()
            }

    except Exception as e:
        logger.error(f"처리 실패: {str(e)}")
        return  {
            "valid": False,
            "message" : f"처리 실패: {str(e)}",
        }
    

#보고서 작성 플로우
@app.post("/make_report")
async def make_report(data: CombinedModel):
    post_data = data.post_data
    report_req = data.report_req
    if report_req.category.title != '정상' or report_req.category.content != '정상':
        await report.init()
        report.report_prompt(post_data)
        report.cell_fill(post_data, report_req)
        time, output_file = report.report_save()
        # formatted_time = time.strftime("%Y-%m-%d %H:%M:%S")
        report_req.create_date = time
        report_req.report_path = output_file

    return {
            "valid": True,
            "message": "보고서 작성 완료",
            "post_data": post_data.model_dump(),
            "report_req": report_req.model_dump()
    }

@app.post("/check_spam")
async def check_spam(request: SpamQuestionRequest):
    """
    게시글 스팸 여부를 확인하는 엔드포인트
    """
    post = request.post_data
    questions = request.question_data.question
    await spam_detector.init()
    filtered_id = await spam_detector.async_check_spam_and_store(post, questions)
    return {
        "valid" : True,
        "status": "success",
        "filtered_id": f"{filtered_id}",
        "message": "게시글 처리 완료"
    }

#이미지 탐지
#임시파일 경로 저장 후 python으로 보냄
#path 경로는 어떻게? ./uploaded_images 이런형태로?
class FilePath(BaseModel):
    file_path : str

@app.post("/upload/")
async def upload_image(file: FilePath):
    
    file_path = file.file_path
    filenames = os.listdir(file_path)
    file_name = filenames[0]
    file_location = os.path.join(file_path, file_name)

    if file_name.lower().endswith((".jpg", ".jpeg", ".png", ".gif", ".bmp")):
        nsfw_score = None
        try:
            image = nd.load_image(file_location)
            # 이미지가 손상되었는지 체크
            try : 
                image.verify()
                
            except Exception as e:
                return {
                    "valid": False,
                    "message" : "이미지 파일이 손상되었거나 유효하지 않습니다.",
                    "file_path" : file_path
                    }
            
            result = image_classifier(image)
            for item in result:
                if item.get("label") == "nsfw":
                    nsfw_score = item.get("score")
                    break
            if nsfw_score is not None and nsfw_score > 0.7:
                results = '악성'
            else : results = '정상'

            return {
                "valid": True,
                "message" : f"이미지 탐지 결과: {results}",
                "file_path" : file_path
            }

        except Exception as e: 
            return {
                "valid": False,
                "message" : str(e),
                "file_path" : file_path
                }
        
        finally : 
            if nsfw_score is not None and nsfw_score < 0.7 :
                os.remove(file_location)
    
    else:
        if not file_name.lower().endswith((".hwp", ".hwpx", ".doc", ".docx", ".pdf")):
            return {
                "valid": False,
                "message" : "유효한 문서 파일이 아닙니다.",
                "file_path" : file_path
                }

        chroma = Chroma("fewshot_chat", OpenAIEmbeddings())
        data = await docu_loader.select_loader(file_location)
        await docu_loader.init()
        llm_chain = await docu_loader.make_llm_text(data)
        combined_text = " ".join(llm_chain)
        content_label = classifier.classify_text(combined_text)
        print(content_label)
        if content_label != '정상':
            os.remove(file_location)
            return{
                "valid": False,
                "message" : "악성 파일로 판단되어 업로드가 차단되었습니다.",
                "file_path" : file_path
            }

        return {
            "valid": True,
            "message" : f"문서 탐지 결과: {content_label}",
            "file_path" : file_path
        }
