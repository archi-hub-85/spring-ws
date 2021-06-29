package ru.akh.spring_ws.dao.exception;

@SuppressWarnings("serial")
public class AuthorNotFoundException extends BookException {

    public AuthorNotFoundException(String msg) {
        super(msg);
    }

    public AuthorNotFoundException(long id) {
        this("Author[id=" + id + "] not found");
    }

}
