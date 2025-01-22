package com.bigProject.tellMe.mapper;

import com.bigProject.tellMe.dto.NoticeDTO;
import com.bigProject.tellMe.entity.Notice;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoticeMapper {
    Notice noDTOToNo(NoticeDTO noticeDTO);
}


