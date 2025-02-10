import pandas as pd
from transformers import AutoTokenizer, AutoModelForSequenceClassification, pipeline

tokenizer = AutoTokenizer.from_pretrained("jason9693/koelectra-small-v3-generator-apeach")
model = AutoModelForSequenceClassification.from_pretrained("jason9693/koelectra-small-v3-generator-apeach")
df = pd.read_csv("APEACH_데이터_라벨링완료.csv", encoding="utf-8-sig")

sampled_data = df.groupby("label_numbers").first().reset_index()
sampled_data = sampled_data.drop(columns = ['is_immoral','label_words'])

#프롬프트용 sample 분리
samples = [{"text": text, "label": label[1]} for text, label in zip(sampled_data['text'], sampled_data['label_numbers'])]

text = df['text']
label = df['label_numbers']

test = [{"text": text, "label": label[1]} for text, label in zip(df['text'], df['label_numbers'])]

pipe = pipeline(
    task="text-classification",
    model=model,
    tokenizer=tokenizer,
    device=0  # GPU 사용, CPU 사용 시 -1로 변경
)

restored_reviews = []

# 테스트 데이터 순회
for data in test:  # `test`는 리스트 또는 시리즈 형태여야 합니다.
    query = data.get('text', '')
    outputs = pipe(query)
    results = outputs[0]['label']
    
    restored_reviews.append(results)
print(restored_reviews)