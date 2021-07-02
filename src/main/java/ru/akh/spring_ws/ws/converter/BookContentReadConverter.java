package ru.akh.spring_ws.ws.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.springframework.core.convert.converter.Converter;

import ru.akh.spring_ws.ws.schema.BookContent;

public class BookContentReadConverter implements Converter<BookContent, ru.akh.spring_ws.dto.BookContent> {

    public static final BookContentReadConverter INSTANCE = new BookContentReadConverter();

    private BookContentReadConverter() {
    }

    @Override
    public ru.akh.spring_ws.dto.BookContent convert(BookContent source) {
        ru.akh.spring_ws.dto.BookContent bookContent = new ru.akh.spring_ws.dto.BookContent();
        bookContent.setId(source.getId());
        bookContent.setFileName(source.getFileName());
        bookContent.setMimeType(source.getMimeType());
        bookContent.setSize(source.getSize());

        byte[] content;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            source.getContent().writeTo(os);
            content = os.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Error while copying content", e);
        }
        bookContent.setContent(content);

        return bookContent;
    }

}
