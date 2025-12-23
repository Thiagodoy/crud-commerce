package com.natixis.commerce.controller;

import com.natixis.commerce.model.UserProduct;
import com.natixis.commerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.natixis.commerce.utils.Url.API_USER_PRODUCT;

@RestController
@RequestMapping(API_USER_PRODUCT)
@RequiredArgsConstructor
public class UerProductController {

    private final ProductService service;

    @GetMapping()
    public ResponseEntity<List<UserProduct>> get() {
        return ResponseEntity.ok(service.getUserProduct());
    }

}
