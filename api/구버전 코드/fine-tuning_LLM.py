import torch
import pandas as pd
from transformers import AutoTokenizer, AutoModelForSequenceClassification, Trainer, TrainingArguments
from torch.utils.data import Dataset


# 모델 및 토크나이저 로드
tokenizer = AutoTokenizer.from_pretrained("beomi/Llama-3-Open-Ko-8B-Instruct-preview")
model = AutoModelForSequenceClassification.from_pretrained(
    "beomi/Llama-3-Open-Ko-8B-Instruct-preview", num_labels=10
)

# 데이터 준비
df = pd.read_csv("APEACH_데이터_라벨링완료.csv", encoding="utf-8-sig")
text = df['text']
label = df['label_numbers']
encodings = tokenizer(text, truncation=True, padding=True, max_length=128, return_tensors="pt")

# Dataset 생성
class CustomDataset(Dataset):
    def __init__(self, encodings, labels):
        self.encodings = encodings
        self.labels = labels

    def __len__(self):
        return len(self.labels)

    def __getitem__(self, idx):
        item = {key: torch.tensor(val[idx]) for key, val in self.encodings.items()}
        item["labels"] = torch.tensor(self.labels[idx])
        return item

dataset = CustomDataset(encodings, label)

# TrainingArguments 설정
training_args = TrainingArguments(
    output_dir="./results",
    evaluation_strategy="steps",
    save_steps=500,
    per_device_train_batch_size=2,
    num_train_epochs=3,
    logging_dir="./logs",
    logging_steps=100,
)

# Trainer 생성
trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=dataset,
)

# 학습 실행
trainer.train()