package ru.akh.spring_ws.ws.client;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;

import ru.akh.spring_ws.dto.Book;
import ru.akh.spring_ws.ws.converter.BookReadConverter;
import ru.akh.spring_ws.ws.converter.BookWriteConverter;
import ru.akh.spring_ws.ws.schema.main.BookField;
import ru.akh.spring_ws.ws.schema.main.GetBooksByAuthorRequest;
import ru.akh.spring_ws.ws.schema.main.GetBooksByAuthorResponse;
import ru.akh.spring_ws.ws.schema.main.GetRequest;
import ru.akh.spring_ws.ws.schema.main.GetResponse;
import ru.akh.spring_ws.ws.schema.main.GetTopBooksRequest;
import ru.akh.spring_ws.ws.schema.main.GetTopBooksResponse;
import ru.akh.spring_ws.ws.schema.main.ObjectFactory;
import ru.akh.spring_ws.ws.schema.main.PutRequest;
import ru.akh.spring_ws.ws.schema.main.PutResponse;

public class BookServiceClient extends AbstractServiceClient {

    private final ObjectFactory factory = new ObjectFactory();

    public BookServiceClient(WebServiceTemplateBuilder builder, String serviceUri) {
        super(builder, serviceUri, "ru.akh.spring_ws.ws.schema.main", false);
    }

    public Book get(long id) {
        GetRequest request = factory.createGetRequest();
        request.setId(id);

        GetResponse response = sendAndReceive(request);
        return BookReadConverter.INSTANCE.convert(response.getResult());
    }

    public long put(Book book) {
        PutRequest request = factory.createPutRequest();
        request.setBook(BookWriteConverter.INSTANCE.convert(book));

        PutResponse response = sendAndReceive(request);
        return response.getResult();
    }

    public List<Book> getTopBooks(Book.Field field, int limit) {
        GetTopBooksRequest request = factory.createGetTopBooksRequest();
        request.setField(BookField.valueOf(field.name()));
        request.setLimit(limit);

        GetTopBooksResponse response = sendAndReceive(request);
        return response.getResult().stream().map(BookReadConverter.INSTANCE::convert).collect(Collectors.toList());
    }

    public List<Book> getBooksByAuthor(String author) {
        GetBooksByAuthorRequest request = factory.createGetBooksByAuthorRequest();
        request.setAuthor(author);

        GetBooksByAuthorResponse response = sendAndReceive(request);
        return response.getResult().stream().map(BookReadConverter.INSTANCE::convert).collect(Collectors.toList());
    }

}
