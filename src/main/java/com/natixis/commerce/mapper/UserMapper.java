package com.natixis.commerce.mapper;

import com.natixis.commerce.controller.request.UserRequest;
import com.natixis.commerce.controller.response.UserResponse;
import com.natixis.commerce.model.User;
import org.mapstruct.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
    User toModel(UserRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(UserRequest request, @MappingTarget User user);


    @AfterMapping
    default User afterMapping(UserRequest request, @MappingTarget User.UserBuilder builder) {
        builder.password(new  BCryptPasswordEncoder().encode(request.getPassword()));
        return builder.build();
    }
}
