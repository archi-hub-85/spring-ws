package ru.akh.spring_ws.ws.converter;

import org.springframework.core.convert.converter.Converter;

import ru.akh.spring_ws.ws.schema.main.Author;
import ru.akh.spring_ws.ws.schema.main.ObjectFactory;

public class AuthorWriteConverter implements Converter<ru.akh.spring_ws.dto.Author, Author> {

    public static final AuthorWriteConverter INSTANCE = new AuthorWriteConverter();

    private final ObjectFactory factory = new ObjectFactory();

    private AuthorWriteConverter() {
    }

    @Override
    public Author convert(ru.akh.spring_ws.dto.Author source) {
        Author author = factory.createAuthor();
        author.setId(source.getId());
        author.setName(source.getName());

        return author;
    }

}
