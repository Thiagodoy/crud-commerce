package com.natixis.commerce.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {
    private String name;
    private String email;
}
