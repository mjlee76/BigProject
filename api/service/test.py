from document_detection import LoadDocumentFile
from langchain_openai import ChatOpenAI
from langchain_openai import OpenAIEmbeddings
from langchain_chroma import Chroma
import asyncio
import document_detection as dd
import os

FILE_PATH = "./service/uploaded_documents"
file_path = os.path.join(os.getcwd() , "service/api_key.txt")
api_key = asyncio.run(dd.load_api_key(file_path))
llm = asyncio.run(dd.selecting_model(api_key))
chroma = Chroma("fewshot_chat", OpenAIEmbeddings())
docu_loader = LoadDocumentFile()
asyncio.run(docu_loader.init())

docu_path = os.path.join(os.getcwd() , "service/uploaded_documents/")
data = docu_loader.select_loader(docu_path)
llm_chain = docu_loader.make_llm_json(data)
