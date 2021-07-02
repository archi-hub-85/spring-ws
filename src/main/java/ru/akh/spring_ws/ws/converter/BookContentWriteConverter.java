package ru.akh.spring_ws.ws.converter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;

import ru.akh.spring_ws.ws.schema.BookContent;
import ru.akh.spring_ws.ws.schema.ObjectFactory;

public class BookContentWriteConverter implements Converter<ru.akh.spring_ws.dto.BookContent, BookContent> {

    public static final BookContentWriteConverter INSTANCE = new BookContentWriteConverter();

    private final ObjectFactory factory = new ObjectFactory();

    private BookContentWriteConverter() {
    }

    @Override
    public BookContent convert(ru.akh.spring_ws.dto.BookContent source) {
        BookContent bookContent = factory.createBookContent();
        bookContent.setId(source.getId());
        bookContent.setFileName(source.getFileName());
        bookContent.setMimeType(source.getMimeType());
        bookContent.setSize(source.getSize());

        DataSource dataSource = new ByteArrayDataSource(source.getContent());
        DataHandler dataHandler = new DataHandler(dataSource);
        bookContent.setContent(dataHandler);

        return bookContent;
    }

    private static class ByteArrayDataSource implements DataSource {

        private final byte[] data;

        public ByteArrayDataSource(byte[] data) {
            this.data = data;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }

        @Override
        public OutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getContentType() {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        @Override
        public String getName() {
            return null;
        }

    }

}
