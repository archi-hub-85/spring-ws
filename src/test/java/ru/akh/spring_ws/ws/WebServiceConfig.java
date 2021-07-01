package ru.akh.spring_ws.ws;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.ws.server.endpoint.interceptor.PayloadLoggingInterceptor;

@TestConfiguration
public class WebServiceConfig {

    @Bean
    public PayloadLoggingInterceptor payloadLoggingInterceptor() {
        return new PayloadLoggingInterceptor();
    }

}
