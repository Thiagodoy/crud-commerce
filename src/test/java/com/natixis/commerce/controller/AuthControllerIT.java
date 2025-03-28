package com.natixis.commerce.controller;

import com.natixis.commerce.controller.request.AuthRequest;
import com.natixis.commerce.controller.request.ProductRequest;
import com.natixis.commerce.controller.response.AuthResponse;
import com.natixis.commerce.controller.response.FaultResponse;
import com.natixis.commerce.controller.response.UserInfoResponse;
import com.natixis.commerce.utils.Url;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerIT {

    @Autowired
    TestRestTemplate restTemplate;

    private static String token;

    @Test
    @Order(1)
    public void shouldLoginSuccessfully() {

        AuthRequest request = AuthRequest.builder()
                .email("natixis@natixis.com")
                .password("123456")
                .build();

        ResponseEntity<AuthResponse> response = this.restTemplate.postForEntity(Url.API_AUTH, request, AuthResponse.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        AuthResponse authResponse = response.getBody();
        token = authResponse.getToken();
        assertNotNull(authResponse);
        assertNotNull(authResponse.getToken());
    }


    @Test
    @Order(2)
    public void shouldReturnUserInfoSuccessfully(){

        HttpEntity<ProductRequest> productRequestHttpEntity = new HttpEntity<>(getHeader());
        ResponseEntity<UserInfoResponse> response = this.restTemplate.exchange(URI.create(Url.API_AUTH.concat("/me")), HttpMethod.GET, productRequestHttpEntity, UserInfoResponse.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        UserInfoResponse userInfoResponse = response.getBody();
        assertNotNull(userInfoResponse);
        assertEquals("natixis", userInfoResponse.getName() );
        assertEquals("natixis@natixis.com", userInfoResponse.getEmail() );
    }


    @Test
    @Order(3)
    public void shouldReturnFaultResponseWithoutToken(){

        HttpEntity<ProductRequest> productRequestHttpEntity = new HttpEntity<>(new HttpHeaders());
        ResponseEntity<UserInfoResponse> response = this.restTemplate.exchange(URI.create(Url.API_AUTH.concat("/me")), HttpMethod.GET, productRequestHttpEntity, UserInfoResponse.class);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @ParameterizedTest
    @CsvSource({
            ",123456",
            "natixis,123456",
            "natixis,12345",
            "natixis@,",
    })
    @Order(4)
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

    private HttpHeaders getHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer ".concat(token));
        return httpHeaders;
    }
}