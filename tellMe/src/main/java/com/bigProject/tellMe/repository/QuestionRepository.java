package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.enumClass.Reveal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // 공개된 게시글만 조회하는 메서드
    Page<Question> findByReveal(Reveal reveal, Pageable pageable);
}
