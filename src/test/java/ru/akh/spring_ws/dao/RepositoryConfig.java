package ru.akh.spring_ws.dao;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import ru.akh.spring_ws.interceptor.DebugInterceptor;

@TestConfiguration
public class RepositoryConfig {

    @Bean
    @Profile("inMemory")
    public BookRepositoryInitializer bookRepositoryInitializer(BookRepository repository) {
        return new BookRepositoryInitializer(repository);
    }

    @Bean("debugInterceptor")
    public DebugInterceptor getDebugInterceptor() {
        return new DebugInterceptor();
    }

    @Bean
    public BeanNameAutoProxyCreator getBeanNameAutoProxyCreator() {
        BeanNameAutoProxyCreator bean = new BeanNameAutoProxyCreator();
        bean.setBeanNames("bookRepository");
        bean.setInterceptorNames("debugInterceptor");
        return bean;
    }

}
