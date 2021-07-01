package ru.akh.spring_ws.ws.client;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ws.client.core.WebServiceTemplate;

import ru.akh.spring_ws.dto.Book;
import ru.akh.spring_ws.dto.BookContent;
import ru.akh.spring_ws.ws.converter.BookReadConverter;
import ru.akh.spring_ws.ws.converter.BookWriteConverter;
import ru.akh.spring_ws.ws.schema.BookField;
import ru.akh.spring_ws.ws.schema.GetBooksByAuthorRequest;
import ru.akh.spring_ws.ws.schema.GetBooksByAuthorResponse;
import ru.akh.spring_ws.ws.schema.GetRequest;
import ru.akh.spring_ws.ws.schema.GetResponse;
import ru.akh.spring_ws.ws.schema.GetTopBooksRequest;
import ru.akh.spring_ws.ws.schema.GetTopBooksResponse;
import ru.akh.spring_ws.ws.schema.ObjectFactory;
import ru.akh.spring_ws.ws.schema.PutRequest;
import ru.akh.spring_ws.ws.schema.PutResponse;

public class BookServiceClient {

    private final WebServiceTemplate template;

    private final ObjectFactory factory = new ObjectFactory();

    public BookServiceClient(WebServiceTemplate template) {
        this.template = template;
    }

    public Book get(long id) {
        GetRequest request = factory.createGetRequest();
        request.setId(id);

        GetResponse response = (GetResponse) template.marshalSendAndReceive(request);
        return BookReadConverter.INSTANCE.convert(response.getResult());
    }

    public long put(Book book) {
        PutRequest request = factory.createPutRequest();
        request.setBook(BookWriteConverter.INSTANCE.convert(book));

        PutResponse response = (PutResponse) template.marshalSendAndReceive(request);
        return response.getResult();
    }

    public List<Book> getTopBooks(Book.Field field, int limit) {
        GetTopBooksRequest request = factory.createGetTopBooksRequest();
        request.setField(BookField.valueOf(field.name()));
        request.setLimit(limit);

        GetTopBooksResponse response = (GetTopBooksResponse) template.marshalSendAndReceive(request);
        return response.getResult().stream().map(BookReadConverter.INSTANCE::convert).collect(Collectors.toList());
    }

    public List<Book> getBooksByAuthor(String author) {
        GetBooksByAuthorRequest request = factory.createGetBooksByAuthorRequest();
        request.setAuthor(author);

        GetBooksByAuthorResponse response = (GetBooksByAuthorResponse) template.marshalSendAndReceive(request);
        return response.getResult().stream().map(BookReadConverter.INSTANCE::convert).collect(Collectors.toList());
    }

    public BookContent getContent(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public void putContent(BookContent content) {
        // TODO Auto-generated method stub
    }

}
