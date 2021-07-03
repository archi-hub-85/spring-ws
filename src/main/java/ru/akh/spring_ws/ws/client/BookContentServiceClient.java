package ru.akh.spring_ws.ws.client;

import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;

import ru.akh.spring_ws.dto.BookContent;
import ru.akh.spring_ws.ws.converter.BookContentReadConverter;
import ru.akh.spring_ws.ws.converter.BookContentWriteConverter;
import ru.akh.spring_ws.ws.schema.content.GetContentRequest;
import ru.akh.spring_ws.ws.schema.content.GetContentResponse;
import ru.akh.spring_ws.ws.schema.content.ObjectFactory;
import ru.akh.spring_ws.ws.schema.content.PutContentRequest;

public class BookContentServiceClient extends AbstractServiceClient {

    private final ObjectFactory factory = new ObjectFactory();

    public BookContentServiceClient(WebServiceTemplateBuilder builder, String serviceUri) {
        super(builder, serviceUri, "ru.akh.spring_ws.ws.schema.content", true);
    }

    public BookContent getContent(long id) {
        GetContentRequest request = factory.createGetContentRequest();
        request.setId(id);

        GetContentResponse response = sendAndReceive(request);
        return BookContentReadConverter.INSTANCE.convert(response.getResult());
    }

    public void putContent(BookContent content) {
        PutContentRequest request = factory.createPutContentRequest();
        request.setContent(BookContentWriteConverter.INSTANCE.convert(content));

        send(request);
    }

}
