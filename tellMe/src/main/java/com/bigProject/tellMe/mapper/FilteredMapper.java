package com.bigProject.tellMe.mapper;

import com.bigProject.tellMe.dto.FilteredDTO;
import com.bigProject.tellMe.entity.Filtered;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FilteredMapper {
    Filtered filToFilDTO(FilteredDTO filteredDTO);
}
