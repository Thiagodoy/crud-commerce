package com.natixis.commerce.exception;

import com.natixis.commerce.utils.MessageStatus;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{
    private MessageStatus status;

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(MessageStatus status) {
        super(status.getDescription());
    }
}
