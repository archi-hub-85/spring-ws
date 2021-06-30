package ru.akh.spring_ws.ws;

import java.util.List;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.interceptor.PayloadLoggingInterceptor;

@TestConfiguration
//@EnableWs
public class WebServiceConfig extends WsConfigurerAdapter {

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(new PayloadLoggingInterceptor());
    }

}
