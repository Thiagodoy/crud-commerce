package com.natixis.commerce.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class CustomPageDTO<T> extends PageImpl<T> {


    public CustomPageDTO(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public CustomPageDTO(List<T> content) {
        super(content, PageRequest.of(0, 1), 10);
    }

    public CustomPageDTO() {
        super(new ArrayList<>());
    }
}
