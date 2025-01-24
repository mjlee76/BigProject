package com.bigProject.tellMe.mapper;

import com.bigProject.tellMe.dto.UserDTO;
import com.bigProject.tellMe.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDTO(User user);
    User userDTOToUser(UserDTO userDTO);
}
