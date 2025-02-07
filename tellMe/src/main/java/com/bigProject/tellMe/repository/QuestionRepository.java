package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.enumClass.Reveal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    // 공개된 게시글만 조회하는 메서드
    Page<Question> findByReveal(Reveal reveal, Pageable pageable);

    List<Question> findByUserId(Long id);

    List<Question> findByUser(User user);

    long countByStatus(Status status);
}
