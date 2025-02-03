import os
from fastapi import FastAPI, UploadFile, File, HTTPException
from sqlalchemy import create_engine, Column, Integer, LargeBinary, String, DateTime
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from sqlalchemy.sql import func
import mysql.connector

mydb = mysql.connector.connect(
    host="127.0.0.1",
    user="root",
    passwd="aivle",
    database="mydatabase"
)

cursor = mydb.cursor()
cursor.execute("CREATE DATABASE IF NOT EXISTS mydatabase")
cursor.execute("USE mydatabase")

app = FastAPI()
# MySQL 데이터베이스 연결 URL
# 사용자(user), 비밀번호(password), 호스트(localhost), 포트(3306), 데이터베이스 이름(mydatabase)
DATABASE_URL = "mysql+pymysql://root:aivle@127.0.0.1:3306/mydatabase"

# SQLAlchemy 엔진 및 세션 생성
engine = create_engine(DATABASE_URL, pool_recycle=3600)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# 파일(이미지) 저장을 위한 테이블 정의
class FileModel(Base):
    __tablename__ = "files"
    id = Column(Integer, primary_key=True, index=True)
    filename = Column(String(255), unique=True, index=True, nullable=False)
    content = Column(LargeBinary, nullable=False)  # 파일의 바이너리 데이터
    uploaded_at = Column(DateTime(timezone=True), server_default=func.now())

Base.metadata.create_all(bind=engine)
@app.post("/upload_db/")
async def upload_file(file: UploadFile = File(...)):
    try:
        # 파일 내용을 바이너리 데이터로 읽기
        file_content = await file.read()
        
        # 데이터베이스 세션 생성
        db = SessionLocal()
        
        # 파일 데이터를 FileModel 인스턴스로 생성
        file_record = FileModel(filename=file.filename, content=file_content)
        
        # DB에 저장
        db.add(file_record)
        db.commit()
        db.refresh(file_record)
        db.close()
        
        return {"message": "파일이 DB에 성공적으로 저장되었습니다.", "file_id": file_record.id}
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))