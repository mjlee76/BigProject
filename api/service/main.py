from typing import List, Dict
from fastapi import FastAPI, BackgroundTasks
from pydantic import BaseModel, Field
from langchain_chroma import Chroma
from langchain_openai import OpenAIEmbeddings

import os
import basic_module as bm
import nsfw_detection as nd
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
file_path = os.getcwd()

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
    user_name: str
    phone : str
    #count : int

# 게시글 정보
class PostBody(BaseModel):
    title: str
    content: str
    user: UserInfo

#DB로 넘길 report 정보
class ReportBody(BaseModel):
    category : List[str] = Field(default_factory=list)
    post_origin_data : Dict[str, str] = Field(default_factory=dict)
    report_path : str = ""
    create_date : str = ""
    
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
# db에 저장할수있게 카테고리를 리스트안에 넣어서 해주세요
# make report()가 안되는 거 수정해야됨

@app.post("/filtered_module")
async def update_item(data: CombinedModel):
    #보고서 작성 플로우
    try:
        post_data = data.post_data
        report_req = data.report_req

        title = post_data.title
        content = post_data.content
        post_origin_data = dict(제목=title, 내용=content) # 원문데이터 저장용
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
                report_req.category = list(title_label.split(","))

                # 팝업 날릴거
                result["제목"] = {"text": f"{title_changed}","경고문": f"{title_label} 감지"}
                result["내용"] = {"text": content}

            elif title_label == '정상' and content_label != '정상':
                content_changed = await changetexter.change_text(content)
                post_data.content =  content_changed
                report_req.category = list(content_label.split(","))

                result["제목"] = {"text": title}
                result["내용"] = {"text": f"{content_changed}","경고문": f"{content_label} 감지"}
            else:
                title_changed = await changetexter.change_text(title)
                post_data.title =  title_changed
                content_changed = await changetexter.change_text(content)
                post_data.content =  content_changed 
                report_req.category = list(set(title_label.split(",") + content_label.split(",")))
                result["제목"] = {"text": f"{title_changed}","경고문": f"{title_label} 감지"}
                result["내용"] = {"text": f"{content_changed}","경고문": f"{content_label} 감지"}
            
            return {
                "valid": True,
                "message": "악성",
                "post_data": post_data.model_dump(),
                "report_req": report_req.model_dump()
            }

        else:
            report_req.category = title_label
            return    {
                "valid": True,
                "message": "원문",
                "post_data": post_data.model_dump(),
                "report_req": report_req.model_dump()
            }

    except Exception as e:
        logger.error(f"처리 실패: {str(e)}")
        return  {
            "valid": False,
            "message" : f"처리 실패: {str(e)}",
        }
        
@app.post("/make_report")        
async def make_report(data: CombinedModel):
        post_data, report_req = data.post_data, data.report_req
        if report_req.category != "정상":
            await report.init()
            report.report_prompt(report_req)
            report.cell_fill(post_data, report_req)
            time, output_file = report.report_save()
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
    try:
        post = request.post_data
        questions = request.question_data.question
        await spam_detector.init()
        spam = await spam_detector.async_check_spam_and_store(post, questions)
        return {
            "valid" : True,
            "spam": spam,
            "message": "게시글 처리 완료"
        }

    except Exception as e:
        logger.error(f"처리 실패: {str(e)}")
        return {
            "valid": False,
            "spam": spam,
            "message" : str(e),
        }

#이미지 탐지
class FilePath(BaseModel):
    file_path : str

@app.post("/upload")
async def upload_image(file: FilePath):
    
    file_path = file.file_path
    filenames = os.listdir(file_path)
    file_name = filenames[0]
    file_location = os.path.join(file_path, file_name)

    if file_name.lower().endswith((".jpg", ".jpeg", ".png", ".gif", ".bmp")):
        nsfw_score = None
        try:
            image = nd.load_image(file_path)
            # 이미지가 손상되었는지 체크
            try : 
                image.verify()
                
            except Exception as e:
                logger.error(f"처리 실패: {str(e)}")
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
                results = "악성"
            else : results = "정상"

            return {
                "valid": True,
                "message" : results,
                "file_path" : file_path
            }

        except Exception as e:
            logger.error(f"처리 실패: {str(e)}")
            return {
                "valid": False,
                "message" : str(e),
                "file_path" : file_path
                }
        
        finally : 
            if nsfw_score is not None and nsfw_score < 0.7 :
                os.remove(file_location)
    
    else:
        if not file_name.lower().endswith((".hwp", ".hwpx", ".doc", ".docx", ".pdf", ".txt")):
            return {
                "valid": False,
                "message" : "유효한 문서 파일이 아닙니다.",
                "file_path" : file_path
                }
        elif file_name.lower().endswith(".txt"):
             file_location = file_path

        chroma = Chroma("fewshot_chat", OpenAIEmbeddings())
        data = await docu_loader.select_loader(file_location)
        await docu_loader.init()
        llm_chain = await docu_loader.make_llm_text(data)
        combined_text = " ".join(llm_chain)
        
        if not combined_text:
            return {
                "valid": False,
                "message": "문서 내용이 없습니다. 올바른 문서를 업로드하세요.",
                "file_path": file_location
            }
        
        content_label = await docu_loader.make_classify_text(combined_text)
        if content_label != '정상':
            if file_name.lower().endswith((".hwp", ".hwpx", ".doc", ".docx", ".pdf")):
                os.remove(file_location)
            elif file_name.lower().endswith(".txt"):
                file_path = file.file_path
                filenames = os.listdir(file_path)
                file_name = filenames[0]
                file_location = os.path.join(file_path, file_name)
                os.remove(file_location)
            return {
                "valid": True,
                "message" : "악성",
                "file_path" : file_path
            }
        else:
            if file_name.lower().endswith((".hwp", ".hwpx", ".doc", ".docx", ".pdf")):
                os.remove(file_location)
            elif file_name.lower().endswith(".txt"):
                file_path = file.file_path
                filenames = os.listdir(file_path)
                file_name = filenames[0]
                file_location = os.path.join(file_path, file_name)
                os.remove(file_location)
            return {
                "valid": True,
                "message" : "정상",
                "file_path" : file_path
            }