package com.natixis.commerce.controller.response;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FaultResponse {
    private int code;
    private String error;
    @Singular
    private List<String> validations;
}
