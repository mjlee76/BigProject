package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.Question;
import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.enumClass.Category;
import com.bigProject.tellMe.enumClass.Reveal;
import com.bigProject.tellMe.enumClass.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    // 공개된 게시글만 조회하는 메서드
    Page<Question> findByReveal(Reveal reveal, Pageable pageable);

    List<Question> findByUserId(Long id);

    List<Question> findByUser(User user);

    long countByStatus(Status status);
    long countByCategory(Category category);
    // 악성 민원 수 (정상이 아닌 카테고리의 민원 수)
    @Query("SELECT COUNT(q) FROM Question q WHERE q.category <> :category")
    long countByCategoryNot(Category category);
    // QuestionRepository에 오늘 날짜와 관련된 쿼리 메서드 추가
    @Query("SELECT COUNT(q) FROM Question q WHERE q.createDate BETWEEN :start AND :end")
    long countByCreateDateBetween(LocalDateTime start, LocalDateTime end);





    @Query("SELECT COUNT(q) FROM Question q WHERE q.category = :category AND q.createDate BETWEEN :start AND :end")
    long countByCategoryAndCreateDateBetween(String category, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.category <> :category AND q.createDate BETWEEN :start AND :end")
    long countByCategoryNotAndCreateDateBetween(String category, LocalDateTime start, LocalDateTime end);
}
