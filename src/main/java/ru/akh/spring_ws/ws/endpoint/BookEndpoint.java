package ru.akh.spring_ws.ws.endpoint;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import ru.akh.spring_ws.dao.BookRepository;
import ru.akh.spring_ws.dto.Book;
import ru.akh.spring_ws.ws.converter.BookReadConverter;
import ru.akh.spring_ws.ws.converter.BookWriteConverter;
import ru.akh.spring_ws.ws.schema.GetBooksByAuthorRequest;
import ru.akh.spring_ws.ws.schema.GetBooksByAuthorResponse;
import ru.akh.spring_ws.ws.schema.GetRequest;
import ru.akh.spring_ws.ws.schema.GetResponse;
import ru.akh.spring_ws.ws.schema.GetTopBooksRequest;
import ru.akh.spring_ws.ws.schema.GetTopBooksResponse;
import ru.akh.spring_ws.ws.schema.ObjectFactory;
import ru.akh.spring_ws.ws.schema.PutRequest;
import ru.akh.spring_ws.ws.schema.PutResponse;

@Endpoint
public class BookEndpoint {

    protected static final String NAMESPACE_URI = "http://akh.ru/spring-ws/books";

    private final ObjectFactory factory = new ObjectFactory();
    private final BookRepository repository;

    public BookEndpoint(BookRepository repository) {
        this.repository = repository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetRequest")
    @ResponsePayload
    public GetResponse get(@RequestPayload GetRequest request) {
        Book book = repository.get(request.getId());

        GetResponse response = factory.createGetResponse();
        response.setResult(BookWriteConverter.INSTANCE.convert(book));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PutRequest")
    @ResponsePayload
    public PutResponse put(@RequestPayload PutRequest request) {
        Book book = BookReadConverter.INSTANCE.convert(request.getBook());
        long id = repository.put(book);

        PutResponse response = factory.createPutResponse();
        response.setResult(id);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetTopBooksRequest")
    @ResponsePayload
    public GetTopBooksResponse getTopBooks(@RequestPayload GetTopBooksRequest request) {
        Book.Field field = Book.Field.valueOf(request.getField().name());
        List<Book> books = repository.getTopBooks(field, request.getLimit());

        GetTopBooksResponse response = factory.createGetTopBooksResponse();
        if (!books.isEmpty()) {
            response.getResult()
                    .addAll(books.stream().map(BookWriteConverter.INSTANCE::convert).collect(Collectors.toList()));
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetBooksByAuthorRequest")
    @ResponsePayload
    public GetBooksByAuthorResponse getBooksByAuthor(@RequestPayload GetBooksByAuthorRequest request) {
        List<Book> books = repository.getBooksByAuthor(request.getAuthor());

        GetBooksByAuthorResponse response = factory.createGetBooksByAuthorResponse();
        if (!books.isEmpty()) {
            response.getResult()
                    .addAll(books.stream().map(BookWriteConverter.INSTANCE::convert).collect(Collectors.toList()));
        }
        return response;
    }

}
