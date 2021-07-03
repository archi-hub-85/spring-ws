package ru.akh.spring_ws.ws.endpoint;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import ru.akh.spring_ws.access.SecuredReader;
import ru.akh.spring_ws.access.SecuredWriter;
import ru.akh.spring_ws.dao.BookRepository;
import ru.akh.spring_ws.dto.BookContent;
import ru.akh.spring_ws.ws.converter.BookContentReadConverter;
import ru.akh.spring_ws.ws.converter.BookContentWriteConverter;
import ru.akh.spring_ws.ws.schema.content.GetContentRequest;
import ru.akh.spring_ws.ws.schema.content.GetContentResponse;
import ru.akh.spring_ws.ws.schema.content.ObjectFactory;
import ru.akh.spring_ws.ws.schema.content.PutContentRequest;

@Endpoint
public class BookContentEndpoint {

    protected static final String NAMESPACE_URI = "http://akh.ru/spring-ws/book-content";

    private final ObjectFactory factory = new ObjectFactory();
    private final BookRepository repository;

    public BookContentEndpoint(BookRepository repository) {
        this.repository = repository;
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
