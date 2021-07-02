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
import ru.akh.spring_ws.dto.BookContent;
import ru.akh.spring_ws.ws.converter.BookContentReadConverter;
import ru.akh.spring_ws.ws.converter.BookContentWriteConverter;
import ru.akh.spring_ws.ws.converter.BookReadConverter;
import ru.akh.spring_ws.ws.converter.BookWriteConverter;
import ru.akh.spring_ws.ws.schema.GetBooksByAuthorRequest;
import ru.akh.spring_ws.ws.schema.GetBooksByAuthorResponse;
import ru.akh.spring_ws.ws.schema.GetContentRequest;
import ru.akh.spring_ws.ws.schema.GetContentResponse;
import ru.akh.spring_ws.ws.schema.GetRequest;
import ru.akh.spring_ws.ws.schema.GetResponse;
import ru.akh.spring_ws.ws.schema.GetTopBooksRequest;
import ru.akh.spring_ws.ws.schema.GetTopBooksResponse;
import ru.akh.spring_ws.ws.schema.ObjectFactory;
import ru.akh.spring_ws.ws.schema.PutContentRequest;
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetContentRequest")
    @ResponsePayload
    @SecuredReader
    public GetContentResponse getContent(@RequestPayload GetContentRequest request) {
        BookContent bookContent = repository.getContent(request.getId());

        GetContentResponse response = factory.createGetContentResponse();
        response.setResult(BookContentWriteConverter.INSTANCE.convert(bookContent));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PutContentRequest")
    @ResponsePayload
    @SecuredWriter
    public void putContent(@RequestPayload PutContentRequest request) {
        BookContent bookContent = BookContentReadConverter.INSTANCE.convert(request.getContent());
        repository.putContent(bookContent);
    }

}
