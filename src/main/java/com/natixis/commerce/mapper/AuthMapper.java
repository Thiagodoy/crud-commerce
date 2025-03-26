package com.natixis.commerce.mapper;

import com.natixis.commerce.controller.response.AuthResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {


    AuthResponse toResponse(String token);

}
