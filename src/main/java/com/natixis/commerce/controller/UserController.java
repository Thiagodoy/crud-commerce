package com.natixis.commerce.controller;


import com.natixis.commerce.controller.request.UserRequest;
import com.natixis.commerce.controller.response.UserResponse;
import com.natixis.commerce.repository.spec.UserSpecification;
import com.natixis.commerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.natixis.commerce.utils.Url.API_USER;

@RestController
@RequestMapping(API_USER)
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<UserResponse> post(@Valid @RequestBody UserRequest request) {
        UserResponse response = service.save(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> put(@PathVariable Long id,@RequestBody UserRequest request) {
        UserResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        UserResponse response = service.get(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> get(
            @RequestParam(required = false) Optional<String> username,
            @RequestParam(required = false) Optional<String> name,
            @RequestParam(required = false) Optional<String> lastName,
            @RequestParam(required = false) Optional<String> email,
            @RequestParam(required = false) Optional<LocalDateTime> createdAt,
            @PageableDefault(sort = {"name"}) Pageable page) {

        UserSpecification spec = UserSpecification.builder()
                .username(username)
                .name(name)
                .lastName(lastName)
                .email(email)
                .createdAt(createdAt)
                .build();

        Page<UserResponse> response = service.get(spec, page);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
