package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.Question;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Integer> {
}
