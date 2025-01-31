package com.bigProject.tellMe.repository;

import com.bigProject.tellMe.dto.NoticeDTO;
import com.bigProject.tellMe.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
//    Page<Notice> findAll(PageRequest id);
}
