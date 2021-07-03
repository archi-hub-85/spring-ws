package ru.akh.spring_ws.ws.client;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class ClientConfig {

    @Bean
    public BookServiceClient bookServiceClient(WebServiceTemplateBuilder builder) {
        return new BookServiceClient(builder, "http://localhost:8080/ws/bookService/");
    }

    @Bean
    public BookContentServiceClient bookContentServiceClient(WebServiceTemplateBuilder builder) {
        return new BookContentServiceClient(builder, "http://localhost:8080/ws/bookContentService/");
    }

}
