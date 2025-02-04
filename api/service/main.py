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

model_path = "./20250204_roberta 파인튜닝"

file_path = "./특이민원보고서_공직자응대매뉴얼.pdf"
pdf_loader = LoadPdfFile(file_path)
tables = pdf_loader.extract_tables_data()
test_table = pdf_loader.make_llm_json()

class UserInfo(BaseModel):
    user_id : str
    user_name: str
    gender : str
    role : str
    birth_date : str
    telephone : str
    address : str
    email: str
    create_date : str
    count : int

class PostBody(BaseModel):
    title: str
    content: str
    user: UserInfo

class ReportBody(BaseModel):
    category : str
    report_path : str
    create_date : object

@app.post("/게시글")
def update_item(post_data: PostBody, report_req: ReportBody): 
    title = post_data.title
    content = post_data.content
    post_origin_data = {"제목": title, "내용": content} # 원문데이터 저장용
    
    classifier = TextClassifier()
    # 분류
    title_label = classifier.classify_text(title)
    content_label = classifier.classify_text(content)
    changetexter = ChangeText()
    result = {}

    if title_label != '정상' or content_label != '정상':
        if title_label != '정상':
            title_changed = changetexter.change_text(title)
            post_data.title =  title_changed
            report_req.category = title_label
            
            # 팝업 날릴거
            result["제목"] = {
                "text": f"{title_changed}",
                "경고문": f"{title_label} 감지"
            }
        else:
            result["제목"] = {
                "text": title
            }
        
        if content_label != '정상':
            content_changed = changetexter.change_text(content)
            post_data.content =  content_changed
            report_req.category = content_label
            
            result["내용"] = {
                "text": f"{content_changed}",
                "경고문": f"{content_label} 감지"
            }
        else:
            result["내용"] = {
                "text": content
            }
        
        result["원문데이터"] = post_origin_data
        # 보고서 생성
        report = MakeReport(file_path)
        report.make_report_detail()
        
        time, output_file = report.report_save()
        report_req.create_date = time
        report_req.report_path = output_file
        
        return post_data, report_req, result
        
    else: 
        post_data.title = title
        post_data.content = content
        return post_data, report_req
    
@app.post("/make_report")
def make_report(report_req: ReportBody):
    def send_report_to_spring():
        url = "http://localhost:8080/spring-endpoint"
        data = report_req
        response = requests.post(url, json=data)
        return response.json()
    
    result = send_report_to_spring()
    return report_req , {"status": "ok", "spring_response": result}
