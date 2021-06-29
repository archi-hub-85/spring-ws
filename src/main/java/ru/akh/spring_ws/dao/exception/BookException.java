package ru.akh.spring_ws.dao.exception;

import org.springframework.core.NestedRuntimeException;

@SuppressWarnings("serial")
public class BookException extends NestedRuntimeException {

    public BookException(String msg) {
        super(msg);
    }

}
