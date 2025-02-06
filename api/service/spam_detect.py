from typing import List, Optional
from fastapi import FastAPI
from pydantic import BaseModel
from typing import TYPE_CHECKING
import torch
from sentence_transformers import SentenceTransformer, util

if TYPE_CHECKING:
    from main import SpamQuestion
    
class SpamDetector:
    def __init__(self, model_name="snunlp/KR-SBERT-V40K-klueNLI-augSTS", threshold=0.8):
        print("모델 로딩 중...")
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.model = SentenceTransformer(model_name, device=self.device)
        self.threshold = threshold
        self.db_posts = {}
        print("모델 로딩 완료!")

    def find_most_relevant_base_id(self, new_emb):
        valid_candidates = []
        
        for question_id, data in self.db_posts.items():
            sim = util.cos_sim(new_emb, data["embedding"]).item()
            if sim >= self.threshold:
                valid_candidates.append((question_id, sim))

        if not valid_candidates:
            return None

        valid_candidates.sort()
        return valid_candidates[0][0]

    def check_spam_and_store(self, spam_question: "SpamQuestion") -> int:
    # 게시글 데이터에서 제목, 내용, 사용자 ID 추출
        new_text = (spam_question.title.strip() + " " + spam_question.content.strip())
        new_emb = self.model.encode(new_text, convert_to_tensor=True, device=self.device)

        most_similar_id = self.find_most_relevant_base_id(new_emb)

        if most_similar_id is None:
            self.db_posts[spam_question.question_id] = {
                "text": new_text,
                "embedding": new_emb
            }
            print(f"새 게시글 {spam_question.question_id} 등록 (기준 글 없음)")
            return spam_question.question_id

        print(f"게시글 {spam_question.question_id}는 기존 게시글 {most_similar_id}과 유사")
        
        return most_similar_id