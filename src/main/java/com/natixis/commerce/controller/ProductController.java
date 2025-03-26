package com.natixis.commerce.controller;

import com.natixis.commerce.controller.request.ProductRequest;
import com.natixis.commerce.controller.response.ProductResponse;
import com.natixis.commerce.controller.response.UserResponse;
import com.natixis.commerce.repository.spec.ProductSpecification;
import com.natixis.commerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Optional;

import static com.natixis.commerce.utils.Url.API_PRODUCT;

@RestController
@RequestMapping(API_PRODUCT)
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<ProductResponse> post(@Valid @RequestBody ProductRequest request, Principal principal) {
        ProductResponse response = service.save(request, principal);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> put(@PathVariable Long id, @RequestBody ProductRequest request) {
        ProductResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable Long id) {
        ProductResponse response = service.get(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> get(
            @RequestParam(required = false) Optional<String> name,
            @RequestParam(required = false) Optional<String> description,
            @RequestParam(required = false) Optional<BigDecimal> price,
            @PageableDefault(sort = {"name"}) Pageable page) {

        ProductSpecification spec = ProductSpecification.builder()
                .name(name)
                .description(description)
                .price(price)
                .build();

        Page<ProductResponse> response = service.get(spec, page);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
