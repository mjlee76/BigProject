from typing import Union, List
from fastapi import FastAPI, File, UploadFile, HTTPException, Body, logger
from pydantic import BaseModel
from langchain_chroma import Chroma
from langchain_openai import OpenAIEmbeddings

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

@app.post("/filtered_module")
async def update_item(data: CombinedModel):
    try:
        post_data = data.post_data
        report_req = data.report_req

        title = post_data.title
        content = post_data.content
        post_origin_data = {"제목": title, "내용": content} # 원문데이터 저장용
        report_req.post_origin_data = post_origin_data
        
        classifier = TextClassifier()
        # 분류
        title_label = classifier.classify_text(title)
        content_label = classifier.classify_text(content)
        changetexter = ChangeText()
        await changetexter.init()
        result = {}

        if title_label != '정상' or content_label != '정상':
            if title_label != '정상':
                title_changed = await changetexter.change_text(title)
                post_data.title =  title_changed
                report_req.category.title = title_label

                # 팝업 날릴거
                result["제목"] = {"text": f"{title_changed}","경고문": f"{title_label} 감지"}
            else:
                result["제목"] = {"text": title}
            
            if content_label != '정상':
                content_changed = await changetexter.change_text(content)
                post_data.content =  content_changed
                report_req.category.content = content_label
                
                result["내용"] = {"text": f"{content_changed}","경고문": f"{content_label} 감지"}
            else:
                result["내용"] = {"text": content}
            
            # 보고서 생성
            return {
                "valid": True,
                "message": "데이터 수신 및 처리 완료",
                "post_data": post_data.model_dump_json(),
                "report_req": report_req.model_dump_json()
            }
            '''post_data, report_req'''
            
        else: 
            '''post_data.title = title
            post_data.content = content
            return post_data, report_req'''
            return    {
                "valid": True,
                "message": "데이터 수신 및 처리 완료",
                "post_data": post_data.model_dump_json(),
                "report_req": report_req.model_dump_json()
            }
    except Exception as e:
        logger.error(f"처리 실패: {str(e)}")
        raise HTTPException(500, "서버 내부 오류")

@app.post("/make_report")
def make_report(data: CombinedModel):
    post_data = data.post_data
    report_req = data.report_req
    
    if report_req.category.title != '정상' or report_req.category.content != '정상':
        report = MakeReport()
        report.report_prompt(post_data)
        report.cell_fill(post_data, report_req)
        time, output_file = report.report_save()
        formatted_time = time.strftime("%Y-%m-%d %H:%M:%S")
        report_req.create_date = formatted_time
        report_req.report_path = output_file
    
    '''def send_report_to_spring():
        url = "http://localhost:8000/"
        data = report_req.model_dump_json()
        response = requests.post(url, json=data)
        return response.json()
    
    result = send_report_to_spring()'''
    return {
            "valid": True,
            "report_req": report_req.dict()
    }
'''report_req , {"status": "ok", "spring_response": result}'''

@app.post("/check_spam/")
def check_spam(request: SpamQuestionRequest):
    """
    게시글 스팸 여부를 확인하는 엔드포인트
    """
    post = request.post_data
    questions = request.question_data.question
    
    filtered_id = spam_detector.check_spam_and_store(post, questions)

    return {
        "status": "success",
        "filtered_id": filtered_id,
        "message": "게시글 처리 완료"
    }

#이미지 탐지
@app.post("/upload/")
async def upload_image(file: UploadFile = File(...)):
    
    if file.filename.lower().endswith((".jpg", ".jpeg", ".png", ".gif", ".bmp")):
        if not file.content_type.startswith("image/"):
            raise HTTPException(status_code=400, detail="유효한 이미지 파일이 아닙니다.")
        file_location = f"{UPLOAD_DIR}/{file.filename}"
        nsfw_score = None
        try:
            async with aiofiles.open(file_location, "wb") as buffer:
                content = await file.read()
                await buffer.write(content)
            image = nd.load_image(UPLOAD_DIR)
            
            # 이미지가 손상되었는지 체크
            try : 
                image.verify()
                
            except Exception as e:
                raise HTTPException(status_code=400, detail="이미지 파일이 손상되었거나 유효하지 않습니다.")
            
            result = image_classifier(image)
            for item in result:
                if item.get("label") == "nsfw":
                    nsfw_score = item.get("score")
                    break
            if nsfw_score is not None and nsfw_score > 0.7:
                os.remove(file_location)
                raise HTTPException(status_code=400, detail="NSFW 이미지로 판단되어 업로드가 차단되었습니다.")

            return {"message": "Success", "result": result}

        except Exception as e: 
            return {"error": str(e)}
        
        finally : 
            if nsfw_score is not None and nsfw_score < 0.7 :
                os.remove(file_location)
    
    else:
        file_location = f"{UPLOAD_DIR}/{file.filename}"
        async with aiofiles.open(file_location, "wb") as buffer:
            content = await file.read()
            await buffer.write(content)
        docu_loader = LoadDocumentFile()
        chroma = Chroma("fewshot_chat", OpenAIEmbeddings())
        data = await docu_loader.select_loader(file_location)
        await docu_loader.init()
        llm_chain = await docu_loader.make_llm_text(data)
        return llm_chain