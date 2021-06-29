package ru.akh.spring_ws.dao.exception;

@SuppressWarnings("serial")
public class BookContentNotFoundException extends BookException {

    public BookContentNotFoundException(String msg) {
        super(msg);
    }

    public BookContentNotFoundException(long id) {
        this("Book[id=" + id + "]'s content not found");
    }

}
