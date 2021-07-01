package ru.akh.spring_ws.ws.client;

import org.apache.wss4j.dom.WSConstants;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

@TestConfiguration
public class ClientConfig {

    @Bean
    public WebServiceTemplate webServiceTemplate(WebServiceTemplateBuilder builder) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("ru.akh.spring_ws.ws.schema");

        Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
        securityInterceptor.setSecurementActions("UsernameToken");
        securityInterceptor.setSecurementPasswordType(WSConstants.PW_DIGEST);

        return builder.setDefaultUri("http://localhost:8080/ws/bookService/")
                .setMarshaller(marshaller)
                .setUnmarshaller(marshaller)
                .interceptors(securityInterceptor)
                .build();
    }

}
