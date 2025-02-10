from document_detection import LoadDocumentFile
from langchain_openai import ChatOpenAI
from langchain_openai import OpenAIEmbeddings
from langchain_chroma import Chroma
import asyncio
import document_detection as dd
import os

FILE_PATH = "./service/uploaded_documents"
file_path = os.path.join(os.getcwd() , "service/api_key.txt")
docu_loader = LoadDocumentFile()
asyncio.run(docu_loader.init())

api_key = asyncio.run(docu_loader.load_api_key(file_path))
llm = asyncio.run(docu_loader.selecting_model(api_key))
chroma = Chroma("fewshot_chat", OpenAIEmbeddings())

docu_path = os.path.join(os.getcwd() , "service/uploaded_documents/")
data = asyncio.run(docu_loader.select_loader(docu_path))
llm_chain = asyncio.run(docu_loader.make_llm_text(data))
print(llm_chain)
