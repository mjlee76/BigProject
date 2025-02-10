package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.entity.Answer;
import com.bigProject.tellMe.entity.Filtered;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilteredRepository extends JpaRepository<Filtered, Long> {
}
