package com.natixis.commerce.controller;

import com.natixis.commerce.controller.dto.CustomPageDTO;
import com.natixis.commerce.controller.request.AuthRequest;
import com.natixis.commerce.controller.request.ProductRequest;
import com.natixis.commerce.controller.request.UserRequest;
import com.natixis.commerce.controller.response.AuthResponse;
import com.natixis.commerce.controller.response.FaultResponse;
import com.natixis.commerce.controller.response.UserResponse;
import com.natixis.commerce.utils.Url;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerIT {

    private static String token;
    @Autowired
    TestRestTemplate restTemplate;

    static Stream<Arguments> provideQueryParameters() {
        return Stream.of(
                Arguments.arguments(UriComponentsBuilder.fromHttpUrl(Url.API_USER)
                        .queryParam("name", "natixis")
                        .toUriString(), 1),
                Arguments.arguments(UriComponentsBuilder.fromHttpUrl(Url.API_USER)
                        .queryParam("name", "natixis")
                        .queryParam("email", "natixis@natixis.com")
                        .toUriString(), 1),
                Arguments.arguments(UriComponentsBuilder.fromHttpUrl(Url.API_USER)
                        .queryParam("email", "natixis@natixis.com")
                        .toUriString(), 1),
                Arguments.arguments(UriComponentsBuilder.fromHttpUrl(Url.API_USER)
                        .queryParam("name", "Charles")
                        .toUriString(), 0),
                Arguments.arguments(UriComponentsBuilder.fromHttpUrl(Url.API_USER)
                        .queryParam("email", "email")
                        .toUriString(), 0)
        );
    }

    public void setUp() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.print(bCryptPasswordEncoder.encode("123456"));
        if (Objects.isNull(token)) {
            AuthRequest request = AuthRequest.builder()
                    .email("natixis@natixis.com")
                    .password("123456")
                    .build();

            ResponseEntity<AuthResponse> response = this.restTemplate.postForEntity(Url.API_AUTH, request, AuthResponse.class);

            token = response.getBody().getToken();
        }
    }

    @ParameterizedTest
    @CsvSource({
            ",123456,Godoy,Thiago",
            "thiagodoy@hotmail.com,,Godoy,Thiago",
            "thiagodoy@hotmail.com,123456,,Thiago",
            "thiagodoy@hotmail.com,123456,Godoy,"
    })
    @Order(1)
    void shouldReturnFaultWithInvalidArgs(String email, String password, String lastName, String name) {
        setUp();
        UserRequest request = UserRequest.builder()
                .email(email)
                .password(password)
                .lastName(lastName)
                .name(name)
                .build();

        HttpHeaders httpHeaders = getHeader();
        HttpEntity<UserRequest> userRequestHttpEntity = new HttpEntity<>(request, httpHeaders);
        ResponseEntity<FaultResponse> response = this.restTemplate.exchange(URI.create(Url.API_USER), HttpMethod.POST, userRequestHttpEntity, FaultResponse.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        FaultResponse faultResponse = response.getBody();
        assertNotNull(faultResponse);
        assertTrue(faultResponse.getValidations().size() > 0);

        if (Objects.isNull(email)) {
            assertTrue(faultResponse.getValidations().get(0).contains("Email"));
        }

        if (Objects.isNull(password)) {
            assertTrue(faultResponse.getValidations().get(0).contains("Password"));
        }

        if (Objects.isNull(lastName)) {
            assertTrue(faultResponse.getValidations().get(0).contains("LastName"));
        }

        if (Objects.isNull(name)) {
            assertTrue(faultResponse.getValidations().get(0).contains("Name"));
        }
    }

    @Test
    @Order(2)
    void shouldUpdateUserNameSuccessfully() {
        setUp();
        UserRequest request = UserRequest.builder()
                .email("thiagodoy@update.com")
                .password("123456")
                .lastName("Godoy")
                .name("Thiago")
                .build();

        HttpHeaders httpHeaders = getHeader();
        HttpEntity<UserRequest> userRequestHttpEntity = new HttpEntity<>(request, httpHeaders);
        ResponseEntity<UserResponse> response = this.restTemplate.exchange(URI.create(Url.API_USER), HttpMethod.POST, userRequestHttpEntity, UserResponse.class);
        UserResponse userResponse = response.getBody();

        request.setLastName("Update");
        HttpEntity<UserRequest> userRequest1 = new HttpEntity<>(request, httpHeaders);
        ResponseEntity<UserResponse> response1 = this.restTemplate.exchange(URI.create(Url.API_USER.concat("/" + userResponse.getId())), HttpMethod.PUT, userRequest1, UserResponse.class);

        assertTrue(response1.getStatusCode().is2xxSuccessful());
        UserResponse userUpdateResponse = response1.getBody();
        assertNotNull(userUpdateResponse);
        assertNotNull(userResponse.getId());
        assertEquals(request.getName(), userUpdateResponse.getName());
    }

    @Test
    @Order(3)
    void shouldGetUserSuccessfully() {
        setUp();
        HttpEntity<UserRequest> userRequestHttpEntity = new HttpEntity<>(getHeader());
        ResponseEntity<UserResponse> response = this.restTemplate.exchange(URI.create(Url.API_USER.concat("/1")), HttpMethod.GET, userRequestHttpEntity, UserResponse.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        UserResponse userResponse = response.getBody();
        assertNotNull(userResponse);
        assertNotNull(userResponse.getId());
        assertEquals(userResponse.getEmail(), "natixis@natixis.com");
        assertEquals(userResponse.getName(), "natixis");
    }

    @Test
    @Order(4)
    void shouldDisableUserSuccessfully() {
        setUp();
        HttpEntity<UserRequest> userRequestHttpEntity = new HttpEntity<>(getHeader());
        this.restTemplate.exchange(URI.create(Url.API_USER.concat("/1")), HttpMethod.DELETE, userRequestHttpEntity, Void.class);

        ResponseEntity<UserResponse> response = this.restTemplate.exchange(URI.create(Url.API_USER.concat("/1")), HttpMethod.GET, userRequestHttpEntity, UserResponse.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        UserResponse userResponse = response.getBody();
        assertNotNull(userResponse);
        assertNotNull(userResponse.getId());
        assertFalse(userResponse.isEnabled());
    }

    @ParameterizedTest
    @Order(4)
    @MethodSource("provideQueryParameters")
    void shouldGetUserUsingPagesSuccessfully(String url, int size) {
        setUp();
        HttpEntity<UserRequest> userRequestHttpEntity = new HttpEntity<>(getHeader());

        ResponseEntity<CustomPageDTO<UserResponse>> response = this.restTemplate.exchange(URI.create(url), HttpMethod.GET, userRequestHttpEntity, new ParameterizedTypeReference<CustomPageDTO<UserResponse>>() {
        });

        assertTrue(response.getStatusCode().is2xxSuccessful());
        PageImpl page = response.getBody();
        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(page.getContent().size(), size);
    }

    @Test
    @Order(5)
    void shouldReturnNotFoundWhenProductIsNotRegistered() {
        setUp();
        HttpEntity<ProductRequest> productRequestHttpEntity = new HttpEntity<>(getHeader());
        ResponseEntity<FaultResponse> response = this.restTemplate.exchange(URI.create(Url.API_USER.concat("/1000")), HttpMethod.GET, productRequestHttpEntity, FaultResponse.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    private HttpHeaders getHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer ".concat(token));
        return httpHeaders;
    }
}