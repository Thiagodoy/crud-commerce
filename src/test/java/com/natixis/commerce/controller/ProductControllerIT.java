package com.natixis.commerce.controller;

import com.natixis.commerce.controller.dto.CustomPageDTO;
import com.natixis.commerce.controller.request.AuthRequest;
import com.natixis.commerce.controller.request.ProductRequest;
import com.natixis.commerce.controller.response.AuthResponse;
import com.natixis.commerce.controller.response.FaultResponse;
import com.natixis.commerce.controller.response.ProductResponse;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerIT {

    private static String token;
    @Autowired
    TestRestTemplate restTemplate;

    static Stream<Arguments> provideQueryParameters() {
        return Stream.of(
                Arguments.arguments(UriComponentsBuilder.fromHttpUrl(Url.API_PRODUCT)
                        .queryParam("name", "phone")
                        .toUriString(), 2),
                Arguments.arguments(UriComponentsBuilder.fromHttpUrl(Url.API_PRODUCT)
                        .queryParam("name", "phone")
                        .queryParam("price", "199.99")
                        .toUriString(), 1),
                Arguments.arguments(UriComponentsBuilder.fromHttpUrl(Url.API_PRODUCT)
                        .queryParam("name", "phone")
                        .queryParam("price", "199.99")
                        .queryParam("description", "headphones")
                        .toUriString(), 1),
                Arguments.arguments(UriComponentsBuilder.fromHttpUrl(Url.API_PRODUCT)
                        .queryParam("description", "RGB backlit mechanical keyboard")
                        .queryParam("price", "129.99")
                        .toUriString(), 1),
                Arguments.arguments(UriComponentsBuilder.fromHttpUrl(Url.API_PRODUCT)
                        .queryParam("description", "Not found")
                        .queryParam("price", "129.98")
                        .toUriString(), 0)
        );
    }

    public void setUp() {
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
            ",Portable Game,1.0",
            "SteamDeck,,1.0",
            "SteamDeck,Portable Game,-1.0",
    })
    @Order(1)
    void shouldReturnFaultWithInvalidArgs(String name, String description, BigDecimal price) {
        setUp();
        ProductRequest request = ProductRequest.builder()
                .price(price)
                .description(description)
                .name(name)
                .build();

        HttpHeaders httpHeaders = getHeader();
        HttpEntity<ProductRequest> productRequestHttpEntity = new HttpEntity<>(request, httpHeaders);
        ResponseEntity<FaultResponse> response = this.restTemplate.exchange(URI.create(Url.API_PRODUCT), HttpMethod.POST, productRequestHttpEntity, FaultResponse.class);

        assertTrue(response.getStatusCode().is4xxClientError());
        FaultResponse faultResponse = response.getBody();
        assertNotNull(faultResponse);
        assertTrue(faultResponse.getValidations().size() > 0);

        if (Objects.isNull(name)) {
            assertTrue(faultResponse.getValidations().stream().anyMatch(v -> v.contains("name")));
        }

        if (Objects.isNull(description)) {
            assertTrue(faultResponse.getValidations().stream().anyMatch(v -> v.contains("description")));
        }

        if (Objects.isNull(price)) {
            assertTrue(faultResponse.getValidations().stream().anyMatch(v -> v.contains("price")));
        }

    }

    @ParameterizedTest
    @CsvSource({
            "4, Laptop, 'High-performance gaming laptop', 99.99",
            "4,, 'High-performance gaming laptop', 99.99",
            "4, Apple Airpod,, 1199.99",
            "4,Laptop, 'High-performance gaming laptop',",
    })
    @Order(2)
    void shouldUpdateProductNameAndPriceSuccessfully(Long id, String name, String description, BigDecimal price) {
        setUp();
        ProductRequest request = ProductRequest.builder()
                .name(name)
                .description(description)
                .price(price)
                .build();

        HttpHeaders httpHeaders = getHeader();
        HttpEntity<ProductRequest> userRequest1 = new HttpEntity<>(request, httpHeaders);
        this.restTemplate.exchange(URI.create(Url.API_PRODUCT.concat("/" + id)), HttpMethod.PUT, userRequest1, ProductResponse.class);

        ResponseEntity<ProductResponse> response = this.restTemplate.exchange(URI.create(Url.API_PRODUCT.concat("/" + id)), HttpMethod.GET, userRequest1, ProductResponse.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        ProductResponse productResponse = response.getBody();
        assertNotNull(productResponse);
        assertEquals(id, productResponse.getId());

        if(Objects.nonNull(name)){
            assertEquals(name, productResponse.getName());
        }

        if(Objects.nonNull(description)){
            assertEquals(description, productResponse.getDescription());
        }

        if(Objects.nonNull(price)){
            assertEquals(price.doubleValue(), productResponse.getPrice().doubleValue());
        }
    }

    @Test
    @Order(3)
    void shouldGetProductSuccessfuly() {
        setUp();
        HttpEntity<ProductRequest> productRequestHttpEntity = new HttpEntity<>(getHeader());
        ResponseEntity<ProductResponse> response = this.restTemplate.exchange(URI.create(Url.API_PRODUCT.concat("/1")), HttpMethod.GET, productRequestHttpEntity, ProductResponse.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        ProductResponse productResponse = response.getBody();
        assertNotNull(productResponse);
        assertNotNull(productResponse.getId());
        assertEquals("Laptop", productResponse.getName());
        assertTrue(productResponse.getPrice().equals(BigDecimal.valueOf(1299.99)));
    }

    @Test
    @Order(4)
    void shouldDisableProductSuccessfully() {
        setUp();
        HttpEntity<ProductRequest> productRequestHttpEntity = new HttpEntity<>(getHeader());
        this.restTemplate.exchange(URI.create(Url.API_PRODUCT.concat("/1")), HttpMethod.DELETE, productRequestHttpEntity, Void.class);

        ResponseEntity<ProductResponse> response = this.restTemplate.exchange(URI.create(Url.API_PRODUCT.concat("/1")), HttpMethod.GET, productRequestHttpEntity, ProductResponse.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        ProductResponse productResponse = response.getBody();
        assertNotNull(productResponse);
        assertNotNull(productResponse.getId());
        assertFalse(productResponse.isEnabled());
    }

    @ParameterizedTest
    @Order(4)
    @MethodSource("provideQueryParameters")
    void shouldGetProductUsingPagesSuccessfully(String url, int size) {
        setUp();
        HttpEntity<ProductRequest> productRequestHttpEntity = new HttpEntity<>(getHeader());

        ResponseEntity<CustomPageDTO<ProductResponse>> response = this.restTemplate.exchange(URI.create(url), HttpMethod.GET, productRequestHttpEntity, new ParameterizedTypeReference<CustomPageDTO<ProductResponse>>() {
        });

        assertTrue(response.getStatusCode().is2xxSuccessful());
        PageImpl page = response.getBody();
        assertNotNull(page);
        assertNotNull(page.getContent());
        assertEquals(page.getSize(), size);
    }

    @Test
    @Order(5)
    void shouldReturnNotFoundWhenProductIsNotRegistered() {
        setUp();
        HttpEntity<ProductRequest> productRequestHttpEntity = new HttpEntity<>(getHeader());
        ResponseEntity<FaultResponse> response = this.restTemplate.exchange(URI.create(Url.API_PRODUCT.concat("/1000")), HttpMethod.GET, productRequestHttpEntity, FaultResponse.class);
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    @Order(6)
    void shouldSaveAProductSuccessfully() {
        setUp();

        ProductRequest request = ProductRequest.builder()
                .name("PS5")
                .description("Video Game")
                .price(BigDecimal.valueOf(399.98))
                .quantity(1L)
                .build();

        HttpEntity<ProductRequest> productRequestHttpEntity = new HttpEntity<>(request, getHeader());
        ResponseEntity<ProductResponse> response = this.restTemplate.exchange(URI.create(Url.API_PRODUCT), HttpMethod.POST, productRequestHttpEntity, ProductResponse.class);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        ProductResponse productResponse = response.getBody();
        assertNotNull(productResponse);
        assertEquals(productResponse.getName(), request.getName());
        assertEquals(productResponse.getDescription(), request.getDescription());
        assertEquals(productResponse.getPrice().doubleValue(), request.getPrice().doubleValue());
    }

    private HttpHeaders getHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer ".concat(token));
        return httpHeaders;
    }
}