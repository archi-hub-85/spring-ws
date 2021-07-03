package ru.akh.spring_ws.ws.client;

import org.apache.wss4j.dom.WSConstants;
import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

abstract class AbstractServiceClient {

    private final Wss4jSecurityInterceptor securityInterceptor;
    private final WebServiceTemplate template;

    protected AbstractServiceClient(WebServiceTemplateBuilder builder, String serviceUri,
            String contextPath, boolean mtomEnabled) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(contextPath);
        marshaller.setMtomEnabled(mtomEnabled);

        securityInterceptor = new Wss4jSecurityInterceptor();
        securityInterceptor.setSecurementActions("UsernameToken");
        securityInterceptor.setSecurementPasswordType(WSConstants.PW_DIGEST);

        this.template = builder.setDefaultUri(serviceUri)
                .setMarshaller(marshaller)
                .setUnmarshaller(marshaller)
                .interceptors(securityInterceptor)
                .build();
    }

    public void setSecurement(String username, String password) {
        securityInterceptor.setSecurementUsername(username);
        securityInterceptor.setSecurementPassword(password);
    }

    protected void send(Object request) {
        template.marshalSendAndReceive(request);
    }

    @SuppressWarnings("unchecked")
    protected <T> T sendAndReceive(Object request) {
        return (T) template.marshalSendAndReceive(request);
    }

}
