package com.natixis.commerce.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {

        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        Map<String, Object> mapError = new HashMap<>();
        mapError.put("messsage", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        mapError.put("code", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mapError.put("error", String.valueOf(errorAttributes.get("message")));

        return mapError;
    }
}
