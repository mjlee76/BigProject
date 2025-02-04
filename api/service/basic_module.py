import os
import re
import torch
from langchain_openai import ChatOpenAI
from langchain.schema import HumanMessage
from transformers import AutoTokenizer, AutoModelForSequenceClassification

#1. api 키 불러오기 및 설정정
def load_api_key(file_path):
    with open(file_path, 'r') as file:
        return file.read().strip()

#2. 모델 설정
def selecting_model(api_key):
    os.environ["OPENAI_API_KEY"] = api_key
    llm = ChatOpenAI(temperature=0, model_name="gpt-4")
    return llm

#3. 모델 프롬프트 출력
class TextClassifier:

    def __init__(self):
        model_path = "./20250204_roberta 파인튜닝"
        self.model = AutoModelForSequenceClassification.from_pretrained(model_path)
        self.tokenizer = AutoTokenizer.from_pretrained("klue/roberta-base")

    def split_text(self, text, max_length):
        """텍스트를 토큰 길이를 기준으로 나눔"""
        tokens = self.tokenizer(text, truncation=False, padding=False)
        input_ids = tokens["input_ids"]
        chunks = [input_ids[i:i + max_length] for i in range(0, len(input_ids), max_length)]
        # 토큰 ID를 다시 텍스트로 디코딩
        return [self.tokenizer.decode(chunk, skip_special_tokens=True) for chunk in chunks if len(chunk) > 0]

    def classify_text(self, text):
        max_length = 512
        chunks = self.split_text(text, max_length - 2)
        results = [] 
        for chunk in chunks:
            try:
                inputs = self.tokenizer(chunk, truncation=True, padding="max_length", max_length=max_length, return_tensors="pt")
                with torch.no_grad():
                    outputs = self.model(**inputs)
                    logits = outputs.logits
                    probabilities = torch.sigmoid(logits).squeeze()  # Sigmoid로 확률 계산
                    results.append(probabilities)
            except Exception as e:
                print(f"Error processing chunk: {chunk}")
                print(f"Error details: {e}")

        if results:
            final_probabilities = torch.mean(torch.stack(results), dim=0)  # 평균 확률
            threshold = 0.5
            predicted_labels = (final_probabilities > threshold).nonzero(as_tuple=True)[0].tolist()

            # 라벨 매핑
            label_mapping = {
                0: "정상", 1: "악성", 2: "욕설",
                3: "외모", 4: "장애인", 5: "인종",
                6: "종교", 7: "지역", 8: "성차별",
                9: "나이", 10: "협박", 11: "성희롱",
            }

            # 결과 출력
            predicted_labels = [label_mapping[label] for label in predicted_labels]
            predicted_labels_text = ', '.join(f'{label}' for label in predicted_labels)
            return predicted_labels_text
        else:
            print("텍스트 조각 처리 중 문제가 발생하여 결과를 생성할 수 없습니다.")

#4. 분류
def extract_label(text):
    if isinstance(text, list) and len(text) == 1:
        return text[0]
    return text

#5. 순화
class ChangeText:
    
    def __init__(self):
        file_path = os.path.join(os.getcwd(), "api_key.txt")
        api_key = load_api_key(file_path)
        self.llm = selecting_model(api_key)
    
    def change_generate_prompt(self, text):
        return (
            f"다음 텍스트를 읽고, 정상이 아닌 텍스트를 정중하고 부드러운 표현으로 순화해주세요:\n"
            f"{text}\n\n"
        )
    
    def change_text(self, text):
        prompt = self.change_generate_prompt(text)
        try:
            # LLM을 사용하여 분류 수행
            response = self.llm([HumanMessage(content=prompt)])
            return response.content.strip()  # LLM 응답 반환
        except Exception as e:
            return f"Error: {str(e)}"


        

        
    
    