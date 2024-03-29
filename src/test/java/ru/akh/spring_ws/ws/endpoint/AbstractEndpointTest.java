package ru.akh.spring_ws.ws.endpoint;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.ws.test.server.MockWebServiceClient;

import ru.akh.spring_ws.AbstractTest;
import ru.akh.spring_ws.dao.BookRepository;
import ru.akh.spring_ws.ws.WebServiceConfig;

@SpringBootTest(properties = "ru.akh.spring-ws.security-enabled=false")
@Import(WebServiceConfig.class)
abstract class AbstractEndpointTest extends AbstractTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    protected BookRepository repository;

    protected MockWebServiceClient mockClient;

    @BeforeEach
    public void createClient() {
        mockClient = MockWebServiceClient.createClient(applicationContext);
    }

}
