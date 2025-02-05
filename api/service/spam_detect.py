from typing import List, Optional
from fastapi import FastAPI
from pydantic import BaseModel
import torch
from sentence_transformers import SentenceTransformer, util

class SpamDetector:
    def __init__(self, model_name="snunlp/KR-SBERT-V40K-klueNLI-augSTS", threshold=0.8, max_duplicates=3):
        print("모델 로딩 중...")
        self.device = "cuda" if torch.cuda.is_available() else "cpu"
        self.model = SentenceTransformer(model_name, device=self.device)
        self.threshold = threshold
        self.max_duplicates = max_duplicates
        self.db_posts = {}
        self.spam_count = {}
        self.banned_posts = set()
        self.post_id_counter = 0
        print("모델 로딩 완료!")

    def find_most_relevant_base_id(self, new_emb, user_id):
        valid_candidates = []
        
        for pid, data in self.db_posts.items():
            if pid in self.banned_posts:
                continue
            sim = util.cos_sim(new_emb, data["embedding"]).item()
            if sim >= self.threshold:
                valid_candidates.append((pid, sim, data["user_id"]))

        if not valid_candidates:
            return None

        for candidate in valid_candidates:
            if candidate[2] == user_id:
                return candidate[0]

        valid_candidates.sort()
        return valid_candidates[0][0]

    def check_spam_and_store(self, title: str, content: str, user_id: str) -> int:
        new_text = (title.strip() + " " + content.strip())
        new_emb = self.model.encode(new_text, convert_to_tensor=True, device=self.device)

        most_similar_id = self.find_most_relevant_base_id(new_emb, user_id)

        if most_similar_id is None:
            most_similar_id = self.post_id_counter + 1

        print(f"\n새 게시글: {title} (기준 글 ID: {most_similar_id}), 작성자: {user_id})")

        if most_similar_id in self.banned_posts:
            print(f"도배 감지! '{title}'가 도배된 기준 글 {most_similar_id}과 유사 (작성자: {user_id}) → 삭제")
            return -1

        if most_similar_id is not None:
            self.spam_count[most_similar_id] = self.spam_count.get(most_similar_id, 0) + 1
            print(f"'{title}' 기존 게시글 {most_similar_id}번과 유사 (누적 {self.spam_count[most_similar_id]}회)")

            if self.spam_count[most_similar_id] >= self.max_duplicates:
                print(f"도배 감지! '{title}'가 {self.spam_count[most_similar_id]}회 올라옴 (작성자: {user_id}) → 삭제")
                self.banned_posts.add(most_similar_id)
                return -1

        self.post_id_counter += 1
        self.db_posts[self.post_id_counter] = {
            "text": new_text,
            "embedding": new_emb,
            "base_id": most_similar_id,
            "user_id": user_id
        }
        print(f"게시글 {self.post_id_counter} 등록 완료 (기준 글 {most_similar_id}, 작성자: {user_id})")
        return self.post_id_counter