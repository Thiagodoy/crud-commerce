package com.natixis.commerce.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class UserResponse {
    private long id;
    private String name;
    private String lastName;
    private String email;
    private boolean enabled;
    private LocalDateTime createdAt;
}
