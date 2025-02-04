from docx import Document
from docx.shared import Pt
from docx.oxml.ns import qn
from docx.oxml import OxmlElement
from docx.enum.text import WD_PARAGRAPH_ALIGNMENT
from docx.enum.table import WD_ALIGN_VERTICAL

from pydantic import BaseModel

import basic_module as bm
import os
import requests

doc = Document("C:/Users/User/Desktop/빅프로젝트/BigProject/api/service/특이민원_발생보고서.docx")

#db에서 받아올 정보
department = "민원복지과"
department_supervisior = "홍길동"
label = '성희롱'
customer = "김태환"
customer_Telephone = "010-1234-4567"

manager = "김영희"
manager_telephone = "02-873-4466"
manager_task = "복지민원 담당"

label_mapping = {
                0: "정상", 1: "악성", 2: "욕설",
                3: "외모", 4: "장애인", 5: "인종",
                6: "종교", 7: "지역", 8: "성차별",
                9: "나이", 10: "협박", 11: "성희롱"}

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

def set_korean_font(paragraph, font_name="맑은 고딕", font_size=12):
    for run in paragraph.runs:
        rPr = run._element.get_or_add_rPr()
        rFonts = OxmlElement("w:rFonts")
        rFonts.set(qn("w:eastAsia"), font_name)
        rPr.append(rFonts)
        run.font.size = Pt(font_size)
    
def add_title(text):
    title = doc.add_paragraph()
    run = title.add_run(text)
    run.bold = True
    run.font.size = Pt(16)
    title.alignment = WD_PARAGRAPH_ALIGNMENT.CENTER

# 표 검색 후 특정 셀 찾기
def cell_fill():
    add_title("특이민원 발생보고서")
    table = doc.tables[0]
    table.cell(0, 1).text = "2025-01-01"  # 예: 발생일자
    table.cell(0, 4).text = f"{department}"
    table.cell(0, 7).text =  f"{department_supervisior}"
    table.cell(4, 1).text = f"{customer}"
    table.cell(4, 4).text = f"{customer_Telephone}"
    table.cell(5, 1).text = f"{manager}"
    table.cell(5, 4).text = f"{manager_telephone}"
    table.cell(5, 7).text = f"{manager_task}"

    if label == '성희롱':
        cell = table.cell(2, 4)
    elif label == '협박':
        cell = table.cell(2, 2)
    else: 
        cell = table.cell(2,1)
    paragraph = cell.paragraphs[0]
    run = paragraph.add_run("○")
    run.font.size = Pt(22)

doc.save("./made_reported/output.docx")