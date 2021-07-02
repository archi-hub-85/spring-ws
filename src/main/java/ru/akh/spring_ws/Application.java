package ru.akh.spring_ws;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.security.WsSecurityValidationException;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.callback.SpringSecurityPasswordValidationCallbackHandler;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "ru.akh.spring-ws.security-enabled", havingValue = "true", matchIfMissing = true)
    @EnableGlobalMethodSecurity(securedEnabled = true)
    public static class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    }

    @Configuration
    @ConditionalOnWebApplication
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Value("${spring.webservices.path}")
        private String webServicesPath;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            String webServicesPattern = webServicesPath + (webServicesPath.endsWith("/") ? "" : "/") + "**";

            http.authorizeRequests()
                    // disable HTTP authorization for web services
                    .antMatchers(webServicesPattern).permitAll()
                    // disable authorization for error page
                    .antMatchers("/error").permitAll()
                    // enable authorization for all other requests
                    .anyRequest().authenticated();

            // disable CSRF support for web services
            http.csrf().ignoringAntMatchers(webServicesPattern);

            http.formLogin();
            http.httpBasic();
        }

    }

    @Configuration
    @ConditionalOnWebApplication
    public class WebServiceConfig extends WsConfigurerAdapter {

        @Autowired
        private ObjectProvider<EndpointInterceptor> customInterceptorsProvider;

        @Override
        public void addInterceptors(List<EndpointInterceptor> interceptors) {
            customInterceptorsProvider.forEach(interceptors::add);
        }

    }

    @Configuration
    @ConditionalOnWebApplication
    @ConditionalOnProperty(name = "ru.akh.spring-ws.security-enabled", havingValue = "true", matchIfMissing = true)
    public static class WsSecurityConfig {

        @Bean
        public SoapFaultMappingExceptionResolver soapFaultMappingExceptionResolver() {
            SoapFaultMappingExceptionResolver exceptionResolver = new SoapFaultMappingExceptionResolver();

            // it's the only one way to log exception with stack trace
            exceptionResolver.setWarnLogCategory("org.springframework.ws.server.endpoint.ExceptionResolverWarnings");

            SoapFaultDefinition defaultFaultDef = new SoapFaultDefinition();
            defaultFaultDef.setFaultCode(SoapFaultDefinition.SERVER);
            exceptionResolver.setDefaultFault(defaultFaultDef);

            Properties exceptionMappings = new Properties();
            exceptionMappings.put(WsSecurityValidationException.class.getName(), SoapFaultDefinition.CLIENT.toString());
            exceptionMappings.put(AccessDeniedException.class.getName(),
                    SoapFaultDefinition.CLIENT.toString() + ",Access is denied");
            exceptionResolver.setExceptionMappings(exceptionMappings);

            return exceptionResolver;
        }

        @Bean
        public Wss4jSecurityInterceptor wsSecurityInterceptor(
                ObjectProvider<CallbackHandler> callbackHandlerProvider,
                SoapFaultMappingExceptionResolver exceptionResolver) {
            Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
            securityInterceptor.setValidationActions("UsernameToken");
            securityInterceptor
                    .setValidationCallbackHandlers(callbackHandlerProvider.stream().toArray(CallbackHandler[]::new));
            securityInterceptor.setExceptionResolver(exceptionResolver);
            return securityInterceptor;
        }

        @Bean
        public CallbackHandler wsAuthenticationHandler(UserDetailsService userDetailsService) {
            SpringSecurityPasswordValidationCallbackHandler callbackHandler = new MySpringSecurityPasswordValidationCallbackHandler();
            callbackHandler.setUserDetailsService(userDetailsService);
            return callbackHandler;
        }

    }

    // We need to return plain password to wss4j
    // TODO find another (elegant) way to fix the initial problem
    public static class MySpringSecurityPasswordValidationCallbackHandler
            extends SpringSecurityPasswordValidationCallbackHandler {

        private static final Pattern PASSWORD_ALGORITHM_PATTERN = Pattern.compile("^\\{(.+)}(.*)$");

        @Override
        protected void handleUsernameToken(WSPasswordCallback callback)
                throws IOException, UnsupportedCallbackException {
            super.handleUsernameToken(callback);

            String password = callback.getPassword();
            if (password != null) {
                Matcher matcher = PASSWORD_ALGORITHM_PATTERN.matcher(password);
                if (matcher.matches()) {
                    String encodingId = matcher.group(1);
                    logger.warn("Password contains encodingId '" + encodingId + "'");
                    if (encodingId.equals("noop")) {
                        String cleanPassword = matcher.group(2);
                        callback.setPassword(cleanPassword);
                    } else {
                        throw new UnsupportedCallbackException(callback, "Cannot decode password");
                    }
                }
            }
        }

    }

}
