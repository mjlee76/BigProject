#!pip install langchain_chroma
#!pip install python-docx
#!pip install openai
#!pip install langchain_openai
#!pip install langchain
#!pip install langchain_teddynote
#!pip install -qU pypdf
#!pip install langchain_community
#!pip install -qU unstructured
#!pip install pdfminer
#!pip install -qU pymupdf
#!pip install -qU rapidocr-onnxruntime
#!pip install -qU pypdf
#!pip install pdfminer.six==20221105
#!pip install pillow-heif
#!pip install pdfplumber langchain

# langchain 기본 라이브러리

import pandas as pd
import openai
import os
import pillow_heif
import re
import json
from datetime import datetime

from langchain_openai import ChatOpenAI
from langchain_openai import OpenAIEmbeddings

from langchain_teddynote.messages import stream_response

from langchain_core.prompts import ChatPromptTemplate, FewShotChatMessagePromptTemplate
from langchain_core.example_selectors import (SemanticSimilarityExampleSelector,)
from langchain_community.document_loaders.parsers.pdf import PDFPlumberParser
from langchain_community.document_loaders import PDFMinerLoader

from langchain_chroma import Chroma
from langchain_community.document_loaders import PyPDFLoader
from langchain_community.document_loaders import UnstructuredPDFLoader
from langchain_community.document_loaders import PDFMinerPDFasHTMLLoader
from langchain.prompts import PromptTemplate
from langchain.chains import LLMChain

# word 파일을 만들기 위한 python-docx
from docx import Document
from docx.oxml.ns import qn
from docx.oxml import OxmlElement
from docx.shared import Pt
from docx.enum.text import WD_PARAGRAPH_ALIGNMENT


FILE_PATH = "./특이민원보고서_공직자응대매뉴얼.pdf"
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
class LoadPdfFile:
    def __init__(self, file_path):
        self.file_path = file_path
        self.data = self.load_pdf_file()
        self.llm = selecting_model(api_key)
        
    def load_pdf_file(self):
        loader = PDFMinerLoader(self.file_path)
        data = loader.load()
        return data

    # 표 데이터 감지 및 추출
    def extract_tables_from_text(self, text):
        tables = []
        rows = text.split("\n")
        for row in rows:
            # 간단한 정규식을 사용해 테이블 형식 감지 (예: 열 구분자가 탭이나 공백인 경우)
            if re.match(r"(\w+\s+)+", row):
                tables.append(row.split())
        return tables
    
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

class MakeReport:
    def __init__(self, file_path):
        self.doc = Document()
        loader = LoadPdfFile(file_path)
        self.report = loader.make_llm_json()

    # 폰트 설정 함수
    def set_korean_font(self, paragraph, font_name="맑은 고딕", font_size=11):
        for run in paragraph.runs:
            rPr = run._element.get_or_add_rPr()
            rFonts = OxmlElement("w:rFonts")
            rFonts.set(qn("w:eastAsia"), font_name)
            rPr.append(rFonts)
            run.font.size = Pt(font_size)
            
    def make_report_detail(self):
        title = self.doc.add_heading("특이민원 발생보고서", level=1)
        title.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER
        table = self.doc.add_table(rows=0, cols=3, style="Table Grid")

        # 첫 번째 행 추가 (헤더)
        header_row = table.add_row().cells
        header_row[0].text = "항목"
        header_row[1].text = "세부 항목"
        header_row[2].text = "내용"
        
        # 항목 추가
        for key, value in self.report.items():
            if isinstance(value, dict):  # 특이민원유형 및 담당자 정보
                for sub_key, sub_value in value.items():
                    row = table.add_row().cells
                    row[0].text = key
                    row[1].text = sub_key
                    row[2].text = str(sub_value)
            else:  # 일반 항목
                row = table.add_row().cells
                row[0].text = key
                row[1].text = ""
                row[2].text = str(value)
    
    def report_save(self):
        time = datetime.now().strftime("%y%m%d_%H%M")
        output_file = f"특이민원_보고서_{time}.docx"
        self.doc.save(output_file)
        print(f"문서가 {output_file}에 저장되었습니다.")