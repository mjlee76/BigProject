# langchain 기본 라이브러리
import pandas as pd
import openai
import os
import asyncio
import aiofiles
import re
import json
import glob

from datetime import datetime
from langchain_openai import ChatOpenAI
from langchain_openai import OpenAIEmbeddings
from langchain_teddynote.messages import stream_response
from langchain_core.prompts import ChatPromptTemplate, FewShotChatMessagePromptTemplate
from langchain_core.example_selectors import (SemanticSimilarityExampleSelector,)

#Loader 불러오기
from langchain_community.document_loaders.parsers.pdf import PDFPlumberParser
from langchain_community.document_loaders import PyPDFLoader
from langchain_community.document_loaders import UnstructuredPDFLoader
from langchain_community.document_loaders import PDFMinerPDFasHTMLLoader
from langchain_community.document_loaders import PDFMinerLoader
from langchain_teddynote.document_loaders import HWPLoader
from langchain_community.document_loaders import Docx2txtLoader
from langchain_community.document_loaders import TextLoader

from langchain_chroma import Chroma
from langchain.prompts import PromptTemplate
from langchain.chains import LLMChain

FILE_PATH = "./api/service/uploaded_documents"
file_path = os.path.join(os.getcwd(), "service/api_key.txt")

# 1. API 키 읽기 및 설정
async def load_api_key(file_path):
    async with aiofiles.open(file_path, 'r') as file:
        return (await file.read()).strip()

#2. 모델 설정
async def selecting_model(api_key):
    os.environ["OPENAI_API_KEY"] = api_key
    llm = await asyncio.to_thread(ChatOpenAI, temperature=0, model_name="gpt-4")
    return llm

# Load the PDF file
class LoadDocumentFile:
    def __init__(self):
        self.file_path = file_path
        self.llm = None

    async def init(self):
        api_key = await load_api_key(file_path)
        self.llm = await selecting_model(api_key)
    
    #loader로 data 저장하기
    def select_loader(self, docu_file_path):
        for filename in os.listdir(docu_file_path):
            docu_path = os.path.join(docu_file_path, filename)
        ext = os.path.splitext(docu_path)[1].lower()
        print(ext)
        # 조건문을 사용하여 확장자에 따라 loader 선택
        if ext == ".pdf":
            loader = PDFMinerLoader(docu_path)
        elif ext in [".hwp", ".hwpx"]:
            loader = HWPLoader(docu_path)
        elif ext in [".doc", ".docx"]:
            loader = Docx2txtLoader(docu_path)
        elif ext == ".txt":
            loader = TextLoader(docu_path)
        else: 
            raise ValueError(f"지원되지 않는 파일 형식: {ext}")
        data = loader.load()
        return data
    
    def make_prompt(self):
        prompt = """
        Extract  the following text and format them as JSON:
        """
        return PromptTemplate(input_variables=["text"], template=prompt)
        
    def make_llm_json(self, data):
        # LLM 초기화 및 체인 생성
        llm_chain = LLMChain(llm=self.llm, prompt=self.make_prompt())
        for doc in data:
            result_text = llm_chain.run({"text": doc.page_content})
            
            try:
                json_match = re.search(r"```json(.*?)```", result_text, re.DOTALL)
                if json_match:
                    json_text = json_match.group(1).strip()
                    result = json.loads(json_text)  # JSON 변환
                    # JSON 데이터에서 "특이민원 발생보고서" 추출
                    return result
                else: print("JSON 형식을 감지하지 못했습니다. LLM 출력:", result_text)
            except json.JSONDecodeError as e:
                print("JSON 변환 중 오류 발생:", e)
                print("LLM 출력:", result_text)
            except KeyError as e:
                print("JSON에서 키를 찾을 수 없음:", e)