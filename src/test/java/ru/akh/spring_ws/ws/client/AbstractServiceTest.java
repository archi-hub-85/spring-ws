package ru.akh.spring_ws.ws.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;

import ru.akh.spring_ws.AbstractTest;
import ru.akh.spring_ws.dao.BookRepository;
import ru.akh.spring_ws.ws.WebServiceConfig;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Import({ SecurityConfig.class, WebServiceConfig.class, ClientConfig.class })
abstract class AbstractServiceTest extends AbstractTest {

    @MockBean
    protected BookRepository repository;

    @Autowired
    private WebServiceTemplate template;

    protected BookServiceClient client;

    @BeforeEach
    void setUp() {
        client = new BookServiceClient(template);
    }

    @BeforeEach
    void setAuth(TestInfo testInfo) {
        WithUser withUser = AnnotationUtils.findAnnotation(testInfo.getTestMethod().get(), WithUser.class);
        if (withUser == null) {
            withUser = AnnotationUtils.findAnnotation(testInfo.getTestClass().get(), WithUser.class);
        }
        if (withUser != null) {
            String username = withUser.username();
            String password = withUser.password();
            logger.debug("with user: username = {}, password = {}", username, password);

            Wss4jSecurityInterceptor securityInterceptor = (Wss4jSecurityInterceptor) template.getInterceptors()[0];
            securityInterceptor.setSecurementUsername(username);
            securityInterceptor.setSecurementPassword(password);
        }
    }

}
