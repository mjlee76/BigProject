package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
