from typing import Union, List
from fastapi import FastAPI, Path, Query
from pydantic import BaseModel

import os
import requests
import xml.etree.ElementTree as ET
import pandas as pd
import datetime
import basic_module as bm

from basic_module import TextClassifier
from basic_module import ChangeText
from make_report_json import LoadPdfFile
from make_report_json import MakeReport

# POST: to create data. GET: to read data. PUT: to update data. DELETE: to delete data.
app = FastAPI()

@app.get("/")
def read_root():
    return {"Hello": "World"}

# Setting environment
path = "./api_key.txt"
api_key = bm.load_api_key(path)
os.environ["OPENAI_API_KEY"] = api_key
llm = bm.selecting_model(api_key)

model_path = "./20250202 klue-bert 파인튜닝(최종본)"

file_path = "./특이민원보고서_공직자응대매뉴얼.pdf"
pdf_loader = LoadPdfFile(file_path)
tables = pdf_loader.extract_tables_data()
test_table = pdf_loader.make_llm_json()


class UserInfo(BaseModel):
    name: str
    email: str

class PostBody(BaseModel):
    title: str
    content: str
    user: UserInfo

class ReportBody(BaseModel):
    category : str
    report_path : str
    create_date : datetime

@app.post("/게시글")
def update_item(post_data: PostBody): 
    title = post_data.title
    content = post_data.content
    post_origin_data = {"제목": title, "내용": content} # 원문데이터 저장용
    
    classifier = TextClassifier()
    # 분류
    title_label = classifier.classify_text(title)
    content_label = classifier.classify_text(content)
    changetexter = ChangeText()
    
    result = {}

    if title_label != ['정상'] or content_label != ['정상']:
        if title_label != ['정상']:
            title_changed = changetexter.change_text(title)
            result["제목"] = {
                "text": f"{title_changed}",
                "label": title_label,
                "경고문": f"{title_label} 감지"
            }
        else:
            result["제목"] = {
                "text": title
            }
        
        if content_label != ['정상']:
            content_changed = changetexter.change_text(content)
            result["내용"] = {
                "text": f"{content_changed}",
                "label": content_label,
                "경고문": f"{content_label} 감지"
            }
        else:
            result["내용"] = {
                "text": content
            }
        
        result["원문데이터"] = post_origin_data
        report = MakeReport(file_path)
        report.make_report_detail()
        report.report_save()
        return result
        
    else: return {"제목": title, "내용": content}
    
@app.post("/make_report")
def make_report(report_req: ReportBody):
    def send_report_to_spring(category, path):
        url = "http://my-spring-server:8080/spring-endpoint"
        data = {
            "category": category,
            "report_path": path,
            "create_date": datetime.now().isoformat()
        }
        response = requests.post(url, json=data)
        return response.json()
    
    # ... 보고서 생성 작업 ...
    category = "민원"
    path = "C:/reports/민원보고서.docx"
    result = send_report_to_spring(category, path)
    return {"status": "ok", "spring_response": result}
