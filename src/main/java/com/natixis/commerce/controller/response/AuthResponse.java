package com.natixis.commerce.controller.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthResponse {

    private String token;
}
