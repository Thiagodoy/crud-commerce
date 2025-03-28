package com.natixis.commerce.mapper;

import com.natixis.commerce.controller.response.AuthResponse;
import com.natixis.commerce.controller.response.UserInfoResponse;
import com.natixis.commerce.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    AuthResponse toResponse(String token);
    UserInfoResponse toResponse(User user);

}
