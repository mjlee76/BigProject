from typing import Union, List
from fastapi import FastAPI, Path, Query
from pydantic import BaseModel

import os
import requests
import xml.etree.ElementTree as ET
import pandas as pd

import basic_module as bm
from basic_module import TextClassifier
from basic_module import ChangeText

# POST: to create data. GET: to read data. PUT: to update data. DELETE: to delete data.
app = FastAPI()
class Item(BaseModel):
    title: str
    content: str
    label : int

@app.get("/")
def read_root():
    return {"Hello": "World"}

# Setting environment
path = "C:/Users/User/Desktop/빅프로젝트/api_key.txt"
api_key = bm.load_api_key(path)
os.environ["OPENAI_API_KEY"] = api_key
llm = bm.selecting_model(api_key)

@app.get("/게시글")
def update_item(title: str, content: str): 
    post_origin_data = {"제목": title, '내용': content} # 원문데이터 저장용
    
    classifier = TextClassifier()
    # 분류
    title_label = classifier.classify_text(title)
    content_label = classifier.classify_text(content)
    
    #분류하고 나서 데이터터
    title_number, title_label_text = bm.classify_sentence(content_label)
    content_number, content_label_text = bm.classify_sentence(content_label)
    if title_number != 0 | content_number != 0:
        changetexter = ChangeText()
        title_changed = changetexter.change_text(title)
        content_changed = changetexter.change_text(content)
        return {
            "제목": {"text": title_changed, "label": title_label, "경고문": title_label_text},
            "내용": {"text": content_changed, "label": content_label, "경고문": content_label_text},
            "원문데이터" : post_origin_data
        }
    # 결과 반환
    else:
        return {
            "제목": {"text": title},
            "내용": {"text": content}
        }

