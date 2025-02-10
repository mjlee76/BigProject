# langchain 기본 라이브러리
import os
import asyncio
import aiofiles
import re
from langchain_openai import ChatOpenAI


#Loader 불러오기
from langchain_community.document_loaders.parsers.pdf import PDFPlumberParser
from langchain_teddynote.document_loaders import HWPLoader
from langchain_community.document_loaders import (
PyPDFLoader, Docx2txtLoader, TextLoader, DirectoryLoader, UnstructuredPDFLoader, 
UnstructuredPDFLoader, PDFMinerPDFasHTMLLoader, PDFMinerLoader)

from langchain_chroma import Chroma
from langchain.prompts import PromptTemplate
from langchain.chains import LLMChain

FILE_PATH = "./api/service/uploaded_documents"
file_path = os.path.join(os.getcwd(), "service/api_key.txt")

# 1. API 키 읽기 및 설정
'''async def load_api_key(file_path):
    async with aiofiles.open(file_path, 'r') as file:
        return (await file.read()).strip()

#2. 모델 설정
async def selecting_model(api_key):
    os.environ["OPENAI_API_KEY"] = api_key
    llm = await asyncio.to_thread(ChatOpenAI, temperature=0, model_name="gpt-4")
    return llm'''

# Load the PDF file
class LoadDocumentFile:
    def __init__(self):
        self.llm = None

    async def init(self):
        api_key = await self.load_api_key(file_path)
        self.llm = await self.selecting_model(api_key)
    
    async def load_api_key(self, file_path):
        async with aiofiles.open(file_path, 'r') as file:
            return (await file.read()).strip()

    async def selecting_model(self, api_key):
        os.environ["OPENAI_API_KEY"] = api_key
        llm = await asyncio.to_thread(ChatOpenAI, temperature=0, model_name="gpt-4")
        return llm
    
    #loader로 data 저장하기
    async def select_loader(self, docu_file_path):
        for filename in os.listdir(docu_file_path):
            docu_path = os.path.join(docu_file_path, filename)
        ext = os.path.splitext(docu_path)[1].lower()
        # 조건문을 사용하여 확장자에 따라 loader 선택
        if ext == ".pdf":
            loader = PyPDFLoader(docu_path)
        elif ext in [".hwp", ".hwpx"]:
            loader = HWPLoader(docu_path)
        elif ext in [".doc", ".docx"]:
            loader = Docx2txtLoader(docu_path)
        elif ext == ".txt":
            text_loader_kwargs = {"autodetect_encoding": True}
            loader = DirectoryLoader(docu_file_path, loader_cls=TextLoader, loader_kwargs=text_loader_kwargs)
        else: 
            raise ValueError(f"지원되지 않는 파일 형식: {ext}")
        data = loader.load()
        return data
    
    async def make_prompt(self):
        prompt = """
        주어진 텍스트에서 주요 항목을 추출한 뒤,
        JSON 형태로 만들어 주세요.
        
        텍스트:
        {text}
        """
        return PromptTemplate(input_variables=["text"], template=prompt)
        
    async def make_llm_text(self, data):
        # LLM 초기화 및 체인 생성
        prompt = await self.make_prompt()
        llm_chain = LLMChain(llm=self.llm, prompt=prompt)
        results = []
        for doc in data:
            result_text = llm_chain.run({"text": doc.page_content})
            cleaned_result = result_text.replace("\n", " ")
            cleaned_result = re.sub(r"\s+", " ", cleaned_result).strip()
            results.append(cleaned_result)
        return results