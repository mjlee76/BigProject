import os
import re
from langchain_openai import ChatOpenAI
from langchain.schema import HumanMessage

#1. api 키 불러오기 및 설정정
def load_api_key(file_path):
    with open(file_path, 'r') as file:
        return file.read().strip()

#2. 모델 설정
def selecting_model(api_key):
    os.environ["OPENAI_API_KEY"] = api_key
    llm = ChatOpenAI(temperature=0, model_name="gpt-4o")
    return llm

#3. 모델 프롬프트 출력
class TextClassifier:

    def __init__(self):
        """
        TextClassifier 초기화.
        Args:
            model (str): 사용할 OpenAI 모델 (기본값: gpt-4).
            api_key (str): OpenAI API 키 (기본값: None). 
        """
        file_path = os.path.join(os.getcwd(), "api_key.txt")
        api_key = load_api_key(file_path)
        self.llm = selecting_model(api_key)
        self.categories = [
            "폭언X(0)", "폭언(1)", "단순욕설(2)", "외모(3)", 
            "학력(4)", "장애인(5)", "인종(6)", 
            "종교(7)", "지역(8)", "성차별(9)", "나이(10)",
            "협박(11)", "성희롱(12)"
        ]

    def generate_prompt(self, text):
        return (
            f"다음 문장을 읽고, 해당 문장이 아래 카테고리 중 어떤 것에 해당하는지 판별하세요:\n"
            f"문장: {text}\n\n"
            f"카테고리: {', '.join(self.categories)}\n\n"
            f"답변 형식: 카테고리 번호와 이름으로만 답변하세요. 예: '폭언X(0) 감지'"
        )

    def classify_text(self, text):
        prompt = self.generate_prompt(text)
        try:
            # LLM을 사용하여 분류 수행
            response = self.llm([HumanMessage(content=prompt)])
            return response.content.strip()  # LLM 응답 반환
        except Exception as e:
            return f"Error: {str(e)}"

#4. 분류
def extract_number(text):
    match = re.search(r"\((\d+)\)", text)
    if match:
        return int(match.group(1))
    return None 

def classify_sentence(label):
    number = extract_number(label)
    match = re.match(r"([^\(]+)\(", label)
    immoral_text = match.group(1).strip()
    return number, immoral_text

#5. 순화
class ChangeText:
    
    def __init__(self):
        file_path = os.path.join(os.getcwd(), "api_key.txt")
        api_key = load_api_key(file_path)
        self.llm = selecting_model(api_key)
    
    def change_generate_prompt(self, text):
        return (
            f"다음 문장을 읽고, 해당 문장에 대한 내용을 순화해주세요:\n"
            f"문장: {text}\n\n"
        )
    
    def change_text(self, text):
        prompt = self.change_generate_prompt(text)
        try:
            # LLM을 사용하여 분류 수행
            response = self.llm([HumanMessage(content=prompt)])
            return response.content.strip()  # LLM 응답 반환
        except Exception as e:
            return f"Error: {str(e)}"


        

        
    
    