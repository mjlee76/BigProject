from typing import Union, List
from fastapi import FastAPI, Path, Query
from pydantic import BaseModel

import os
import requests
import xml.etree.ElementTree as ET
import pandas as pd
import openai
from openai import OpenAI
import json
import sys
import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
from datasets import load_dataset, Dataset
import emergency as em

# POST: to create data. GET: to read data. PUT: to update data. DELETE: to delete data.
app = FastAPI()
class Item(BaseModel):
        hospitalName: str
        address : str
        emergencyMedicalInstitutionType: str 
        phoneNumber1 : str
        phoneNumber3 : str
        latitude: float
        longitude: float
        distance : float

@app.get("/deploy_test")
def read_root():
    return {"Hello": "World"}

@app.get("/")
def read_root():
    return {"Hello": "World"}

# Setting environment
path = os.getcwd() + "/"
sys.path.append(path)
audio_path = path + 'audio/'
save_directory = path + 'fine_tuned_bert_our/'
emergency = pd.read_csv(path + '응급실 정보.csv')
map_key = em.load_file(path + 'map_key.txt')
map_key = json.loads(map_key)
c_id, c_key = map_key['c_id'], map_key['c_key']

patient_requests = []
def create_dataframe():
    if patient_requests:
        openai.api_key = em.load_file(path + '/api_key.txt')
        os.environ['OPENAI_API_KEY'] = openai.api_key
        df = em.audio2text(audio_path)
        df = em.text2summary(df)
        df_location = pd.DataFrame({'위도': [data['latitude'] for data in patient_requests],
                                    '경도': [data['longitude'] for data in patient_requests]})
        df = pd.concat((df, df_location), axis=1)
        return df
    
def recommentation():
    if patient_requests:
        df = create_dataframe()
        pred = em.model_prediction(save_directory, df)
        result = em.recom_em(path, df, num)
        if pred < 3:
            addr = []
            for i in range(len(result)):
                hospital_name = result[i][1]
                addr.append(emergency.loc[emergency['병원이름'] == hospital_name].iloc[0])
            return result, addr
        else:
            return {"message" : "집에서 휴식을 취하세요"} 

@app.get("/hospital_by_module")
def update_item(request: str, latitude: float, longitude: float): 
    patient_data = {"request": request, 'latitude': latitude, 'longitude': longitude}
    patient_requests.append(patient_data)
    # df = create_dataframe() 
    result, addr = recommentation()
    items_data = [
            {
            "hospitalName": addr[i]['병원이름'],
            "address": addr[i]['주소'],
            "emergencyMedicalInstitutionType": addr[i]['응급의료기관 종류'],
            "phoneNumber1": addr[i]['전화번호 1'],
            "phoneNumber3": addr[i]['전화번호 3'],
            "latitude": addr[i]['위도'],
            "longitude": addr[i]['경도'],
            "distance": float(result[i][0] / 1000)
            }
            for i in range(len(result))
        ]
    return items_data
