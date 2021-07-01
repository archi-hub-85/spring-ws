package ru.akh.spring_ws.ws.client;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class SecurityConfig {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails reader = User.withUsername(UsersConstants.READER_USERNAME)
                .password("{noop}" + UsersConstants.READER_PASSWORD)
                .roles("READER")
                .build();
        UserDetails writer = User.withUsername(UsersConstants.WRITER_USERNAME)
                .password("{noop}" + UsersConstants.WRITER_PASSWORD)
                .roles("WRITER")
                .build();
        UserDetails admin = User.withUsername(UsersConstants.ADMIN_USERNAME)
                .password("{noop}" + UsersConstants.ADMIN_PASSWORD)
                .roles("READER", "WRITER")
                .build();
        return new InMemoryUserDetailsManager(reader, writer, admin);
    }

}
