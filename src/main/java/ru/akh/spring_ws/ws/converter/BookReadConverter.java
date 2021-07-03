package ru.akh.spring_ws.ws.converter;

import org.springframework.core.convert.converter.Converter;

import ru.akh.spring_ws.ws.schema.main.Book;

public class BookReadConverter implements Converter<Book, ru.akh.spring_ws.dto.Book> {

    public static final BookReadConverter INSTANCE = new BookReadConverter();

    private BookReadConverter() {
    }

    @Override
    public ru.akh.spring_ws.dto.Book convert(Book source) {
        ru.akh.spring_ws.dto.Book book = new ru.akh.spring_ws.dto.Book();
        book.setId(source.getId());
        book.setTitle(source.getTitle());
        book.setYear(source.getYear());

        ru.akh.spring_ws.dto.Author author = AuthorReadConverter.INSTANCE.convert(source.getAuthor());
        book.setAuthor(author);

        return book;
    }

}
