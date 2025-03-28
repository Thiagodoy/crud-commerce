package com.natixis.commerce.service;

import com.natixis.commerce.config.JwtService;
import com.natixis.commerce.controller.request.AuthRequest;
import com.natixis.commerce.controller.response.AuthResponse;
import com.natixis.commerce.controller.response.UserInfoResponse;
import com.natixis.commerce.mapper.AuthMapper;
import com.natixis.commerce.model.User;
import com.natixis.commerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final AuthMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = repository.findByEmail(request.getEmail()).orElseThrow();
        return this.mapper.toResponse(jwtService.generateToken(user));
    }

    public UserInfoResponse getUserInfo(Principal principal){
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        return mapper.toResponse(user);
    }
}
