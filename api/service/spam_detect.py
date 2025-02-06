from typing import List, Optional
from fastapi import FastAPI
from pydantic import BaseModel
from typing import TYPE_CHECKING
import torch
from sentence_transformers import SentenceTransformer, util

if TYPE_CHECKING:
    from main import PostData, QuestionApiDTO
    
class SpamDetector:
    def __init__(self, model_name="snunlp/KR-SBERT-V40K-klueNLI-augSTS", threshold=0.8):
        print("모델 로딩 중...")
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.model = SentenceTransformer(model_name, device=self.device)
        self.threshold = threshold
        self.db_posts = {}
        print("모델 로딩 완료!")

    def find_most_relevant_base_id(self, new_emb, questions: List["QuestionApiDTO"]):
        valid_candidates = []
        
        for question in questions:
            existing_emb = self.model.encode(question.title + " " + question.content, convert_to_tensor=True, device=self.device)
            sim = util.cos_sim(new_emb, existing_emb).item()
            if sim >= self.threshold:
                valid_candidates.append((question.id, sim))

        if not valid_candidates:
            return None  # 유사한 기준 글이 없으면 새로운 기준으로 등록

        # 유사도 높은 질문 중 가장 오래된 ID 반환
        valid_candidates.sort()
        return valid_candidates[0][0]

    def check_spam_and_store(self, post_data: "PostData", questions: List["QuestionApiDTO"]) -> int:
    # 게시글 데이터에서 제목, 내용, 사용자 ID 추출
        new_text = (post_data.title.strip() + " " + post_data.content.strip())
        new_emb = self.model.encode(new_text, convert_to_tensor=True, device=self.device)

        most_similar_id = self.find_most_relevant_base_id(new_emb, questions)

        if most_similar_id is None:
            # 새로운 질문으로 등록
            new_id = max([q.id for q in questions], default=0) + 1  # 새로운 ID 생성
            self.db_posts[new_id] = {
                "text": new_text,
                "embedding": new_emb
            }
            print(f"새 게시글 {new_id} 등록 (기준 글 없음)")
            return new_id

        print(f"게시글이 기존 질문 {most_similar_id}과 유사")
        return most_similar_id