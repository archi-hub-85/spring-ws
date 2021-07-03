package ru.akh.spring_ws.ws.endpoint;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.ws.test.server.MockWebServiceClient;

import ru.akh.spring_ws.AbstractTest;
import ru.akh.spring_ws.dao.BookRepository;
import ru.akh.spring_ws.ws.WebServiceConfig;

@SpringBootTest(properties = "ru.akh.spring-ws.security-enabled=false")
@Import(WebServiceConfig.class)
abstract class AbstractEndpointTest extends AbstractTest {

    protected static final String NAMESPACE_URI = BookEndpoint.NAMESPACE_URI;

    @Autowired
    private ApplicationContext applicationContext;

    @Value("classpath:wsdl/book.xsd")
    protected Resource schemaResource;

    @MockBean
    protected BookRepository repository;

    protected MockWebServiceClient mockClient;

    @BeforeEach
    public void createClient() {
        mockClient = MockWebServiceClient.createClient(applicationContext);
    }

}
