package com.natixis.commerce.controller;

import com.natixis.commerce.controller.request.AuthRequest;
import com.natixis.commerce.controller.response.AuthResponse;
import com.natixis.commerce.controller.response.FaultResponse;
import com.natixis.commerce.utils.Url;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIT {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void shouldLoginSuccessfully() {

        AuthRequest request = AuthRequest.builder()
                .email("natixis@natixis.com")
                .password("123456")
                .build();

        ResponseEntity<AuthResponse> response = this.restTemplate.postForEntity(Url.API_AUTH, request, AuthResponse.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        AuthResponse authResponse = response.getBody();
        assertNotNull(authResponse);
        assertNotNull(authResponse.getToken());
    }

    @ParameterizedTest
    @CsvSource({
            ",123456",
            "natixis,123456",
            "natixis,12345",
            "natixis@,",

    })
    public void shouldReturnFaultResponseWithInvalidArgs(String email, String password){
        AuthRequest request = AuthRequest.builder()
                .email(email)
                .password(password)
                .build();

        ResponseEntity<FaultResponse> response = this.restTemplate.postForEntity(Url.API_AUTH, request, FaultResponse.class);
        FaultResponse faultResponse = response.getBody();
        assertTrue(response.getStatusCode().is4xxClientError());
        assertNotNull(faultResponse);
        assertFalse(faultResponse.getValidations().isEmpty());

        if (Objects.isNull(email)) {
            assertTrue(faultResponse.getValidations().stream().anyMatch(v-> v.contains("email")));
        }

        if (Objects.isNull(password)) {
            assertTrue(faultResponse.getValidations().stream().anyMatch(v-> v.contains("password")));
        }
    }
}