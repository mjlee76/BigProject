# langchain 기본 라이브러리
import pandas as pd
import openai
import os
# import pillow_heif
import re
import json
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
file_path = os.path.join(os.getcwd(), "api_key.txt")

# 1. API 키 읽기 및 설정
def load_api_key(file_path):
    with open(file_path, 'r') as file:
        return file.read().strip()
    
def selecting_model(api_key):
    os.environ["OPENAI_API_KEY"] = api_key
    llm = ChatOpenAI(temperature=0, model_name="gpt-4o")
    return llm

api_key = load_api_key("api_key.txt")
llm = selecting_model(api_key)
chroma = Chroma("fewshot_chat", OpenAIEmbeddings())

# Load the PDF file
class LoadDocumentFile:
    def __init__(self, file_path):
        self.file_path = file_path
        self.data = self.load_pdf_file()
        self.llm = selecting_model(api_key)
    
    #loader로 data 저장하기
    def load_pdf_file(self):
        loader = PDFMinerLoader(self.file_path)
        data = loader.load()
        return data
    
    def load_hwp_file(self):
        loader = HWPLoader(self.file_path)
        data = loader.load()
        return data
    
    def load_word_file(self):
        loader = Docx2txtLoader(self.file_path)
        data = loader.load()
        return data
    
    def load_word_file(self):
        loader = TextLoader(self.file_path)
        data = loader.load()
        return data


    def extract_tables_data(self):
        all_tables = []
        for doc in self.data:
            text_content = doc.page_content  # 페이지 내용
            tables = self.extract_tables_from_text(text_content)
            all_tables.extend(tables)
        return all_tables
    
    def make_prompt(self):
        table_extraction_prompt = """
        Extract all tables from the following text and format them as JSON:
        {text}
        """
        return PromptTemplate(input_variables=["text"], template=table_extraction_prompt)
        
    def make_llm_json(self):
        # LLM 초기화 및 체인 생성
        llm_chain = LLMChain(llm=self.llm, prompt=self.make_prompt())
        for doc in self.data:
            result_text = llm_chain.run({"text": doc.page_content})
            
            try:
                json_match = re.search(r"```json(.*?)```", result_text, re.DOTALL)
                if json_match:
                    json_text = json_match.group(1).strip()
                    result = json.loads(json_text)  # JSON 변환
                    report = result.get("특이민원 발생보고서", {})
                    # JSON 데이터에서 "특이민원 발생보고서" 추출
                    return report
                else: print("JSON 형식을 감지하지 못했습니다. LLM 출력:", result_text)
            except json.JSONDecodeError as e:
                print("JSON 변환 중 오류 발생:", e)
                print("LLM 출력:", result_text)
            except KeyError as e:
                print("JSON에서 키를 찾을 수 없음:", e)