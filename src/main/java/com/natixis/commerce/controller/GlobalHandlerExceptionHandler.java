package com.natixis.commerce.controller;

import com.natixis.commerce.controller.response.FaultResponse;
import com.natixis.commerce.exception.ServiceException;
import com.natixis.commerce.utils.MessageStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalHandlerExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<FaultResponse> handleNoSuchElement(ServiceException ex, WebRequest request) {
        log.error("method: handleGenericException, request={}, message={}", request, ex.getMessage(), ex);

        FaultResponse faultResponse = FaultResponse.builder()
                .error(ex.getMessage())
                .code(MessageStatus.RECORD_NOT_FOUND.equals(ex.getStatus())
                        ? HttpStatus.NOT_FOUND.value() : HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(faultResponse);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<FaultResponse> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("method: handleGenericException, request={}, message={}", request, ex.getMessage(), ex);

        FaultResponse faultResponse = FaultResponse.builder()
                .error(ex.getMessage())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(faultResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.error("method: handleMethodArgumentNotValid, request={}, message={}", request, ex.getMessage(), ex);

        FaultResponse.FaultResponseBuilder faultResponseBuilder = FaultResponse.builder()
                .error("Validation failed")
                .code(HttpStatus.BAD_REQUEST.value());

        ex.getBindingResult().getAllErrors()
                .forEach((error) -> {
                    String field = ((FieldError) error).getField();
                    faultResponseBuilder.validation(String.format("%s %s", field, error.getDefaultMessage()));
                });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(faultResponseBuilder.build());
    }
}
