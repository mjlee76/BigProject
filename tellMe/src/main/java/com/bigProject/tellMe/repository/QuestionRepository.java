package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
