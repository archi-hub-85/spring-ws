package ru.akh.spring_ws.ws.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ws.soap.client.SoapFaultClientException;

import ru.akh.spring_ws.AbstractTest;
import ru.akh.spring_ws.dao.BookRepository;
import ru.akh.spring_ws.ws.WebServiceConfig;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Import({ SecurityConfig.class, WebServiceConfig.class, ClientConfig.class })
abstract class AbstractServiceTest<T extends AbstractServiceClient> extends AbstractTest {

    @MockBean
    protected BookRepository repository;

    @Autowired
    protected T client;

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

            client.setSecurement(username, password);
        }
    }

    protected void assertThrowsSoapFault(String expectedFaultCode, String expectedFaultStringOrReason,
            Executable executable) {
        SoapFaultClientException exception = Assertions.assertThrows(SoapFaultClientException.class, executable);
        logger.debug("exception = {}", exception.toString());

        Assertions.assertEquals(expectedFaultCode, exception.getFaultCode().getLocalPart(), "faultCode");
        if (expectedFaultStringOrReason != null) {
            Assertions.assertEquals(expectedFaultStringOrReason, exception.getFaultStringOrReason(),
                    "faultStringOrReason");
        }
    }

}
