package com.natixis.commerce.controller;

import com.natixis.commerce.controller.request.AuthRequest;
import com.natixis.commerce.controller.response.AuthResponse;
import com.natixis.commerce.controller.response.UserInfoResponse;
import com.natixis.commerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.natixis.commerce.utils.Url.API_AUTH;

@RestController
@RequestMapping(API_AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = service.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getUserInfo(Principal principal) {
        UserInfoResponse response = service.getUserInfo(principal);
        return ResponseEntity.ok(response);
    }
}
