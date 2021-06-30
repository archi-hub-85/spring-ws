package ru.akh.spring_ws.ws.converter;

import org.springframework.core.convert.converter.Converter;

import ru.akh.spring_ws.ws.schema.Author;
import ru.akh.spring_ws.ws.schema.Book;
import ru.akh.spring_ws.ws.schema.ObjectFactory;

public class BookWriteConverter implements Converter<ru.akh.spring_ws.dto.Book, Book> {

    public static final BookWriteConverter INSTANCE = new BookWriteConverter();

    private final ObjectFactory factory = new ObjectFactory();

    private BookWriteConverter() {
    }

    @Override
    public Book convert(ru.akh.spring_ws.dto.Book source) {
        Book book = factory.createBook();
        book.setId(source.getId());
        book.setTitle(source.getTitle());
        book.setYear(source.getYear());

        Author author = AuthorWriteConverter.INSTANCE.convert(source.getAuthor());
        book.setAuthor(author);

        return book;
    }

}
