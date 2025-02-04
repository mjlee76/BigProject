from docx import Document
from docx.shared import Pt
import basic_module as bm
import os
import requests

doc = Document("C:/Users/User/Desktop/빅프로젝트/BigProject/api/service/특이민원_발생보고서.docx")
'''path = "./api_key.txt"
api_key = bm.load_api_key(path)
os.environ["OPENAI_API_KEY"] = api_key
llm = bm.selecting_model(api_key)'''

def get_post_result(title: str, content: str):
    url = "http://localhost:8000/게시글"  # FastAPI 서버 주소
    params = {"title": title, "content": content}
    response = requests.get(url, params=params)
    if response.ok:
        return response.json()
    else:
        return {"error": response.text}
get_post_result()

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

# 표 검색 후 특정 셀 찾기
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