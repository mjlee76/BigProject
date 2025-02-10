package com.bigProject.tellMe.mapper;

import com.bigProject.tellMe.dto.ReportDTO;
import com.bigProject.tellMe.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    // Entity → DTO 변환
    ReportDTO toDto(Report entity);

    Report repoDTOTORepo(ReportDTO reportDTO);
}
