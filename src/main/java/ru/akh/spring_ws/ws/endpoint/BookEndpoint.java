package ru.akh.spring_ws.ws.endpoint;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import ru.akh.spring_ws.access.SecuredReader;
import ru.akh.spring_ws.access.SecuredWriter;
import ru.akh.spring_ws.dao.BookRepository;
import ru.akh.spring_ws.dto.Book;
import ru.akh.spring_ws.ws.converter.BookReadConverter;
import ru.akh.spring_ws.ws.converter.BookWriteConverter;
import ru.akh.spring_ws.ws.schema.main.GetBooksByAuthorRequest;
import ru.akh.spring_ws.ws.schema.main.GetBooksByAuthorResponse;
import ru.akh.spring_ws.ws.schema.main.GetRequest;
import ru.akh.spring_ws.ws.schema.main.GetResponse;
import ru.akh.spring_ws.ws.schema.main.GetTopBooksRequest;
import ru.akh.spring_ws.ws.schema.main.GetTopBooksResponse;
import ru.akh.spring_ws.ws.schema.main.ObjectFactory;
import ru.akh.spring_ws.ws.schema.main.PutRequest;
import ru.akh.spring_ws.ws.schema.main.PutResponse;

@Endpoint
public class BookEndpoint {

    protected static final String NAMESPACE_URI = "http://akh.ru/spring-ws/book";

    private final ObjectFactory factory = new ObjectFactory();
    private final BookRepository repository;

    public BookEndpoint(BookRepository repository) {
        this.repository = repository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetRequest")
    @ResponsePayload
    @SecuredReader
    public GetResponse get(@RequestPayload GetRequest request) {
        Book book = repository.get(request.getId());

        GetResponse response = factory.createGetResponse();
        response.setResult(BookWriteConverter.INSTANCE.convert(book));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PutRequest")
    @ResponsePayload
    @SecuredWriter
    public PutResponse put(@RequestPayload PutRequest request) {
        Book book = BookReadConverter.INSTANCE.convert(request.getBook());
        long id = repository.put(book);

        PutResponse response = factory.createPutResponse();
        response.setResult(id);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetTopBooksRequest")
    @ResponsePayload
    @SecuredReader
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
    @SecuredReader
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
