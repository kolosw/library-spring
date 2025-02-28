package com.patiun.libraryspring.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.patiun.libraryspring.user.Authority.*;

@Configuration
@EnableWebSecurity
public class RestSecurityConfig {

    public static final String REACT_FRONT_END_URL = "http://localhost:3000";
    public static final String ANGULAR_FRONT_END_URL = "http://localhost:4200";

    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public RestSecurityConfig(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST, "/books/**").hasAuthority(ADD_BOOKS.name())
                        .requestMatchers(HttpMethod.POST, "/orders/**").hasAuthority(PLACE_ORDERS.name())
                        .requestMatchers(HttpMethod.GET, "/books/**").hasAuthority(READ_BOOKS.name())
                        .requestMatchers(HttpMethod.GET, "/users/auth").authenticated()
                        .requestMatchers(HttpMethod.GET, "/users/**").hasAuthority(READ_USERS.name())
                        .requestMatchers(HttpMethod.GET, "/orders/**").hasAuthority(READ_ORDERS.name())
                        .requestMatchers(HttpMethod.PUT, "/books/**").hasAuthority(EDIT_BOOKS.name())
                        .requestMatchers(HttpMethod.DELETE, "/books/**").hasAuthority(EDIT_BOOKS.name())
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasAuthority(EDIT_USERS.name())
                        .requestMatchers(HttpMethod.PUT, "/orders/*/collect", "/orders/*/return").hasAuthority(COLLECT_ORDERS.name())
                        .requestMatchers(HttpMethod.PUT, "/orders/*/approve", "/orders/*/decline").hasAuthority(JUDGE_ORDERS.name())
                        .anyRequest().permitAll()
                )
                .httpBasic((httpSecurityHttpBasicConfigurer) -> httpSecurityHttpBasicConfigurer.authenticationEntryPoint(authenticationEntryPoint))
                .cors().and()
                .build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@Nullable CorsRegistry registry) {
                if (registry == null) {
                    return;
                }
                registry.addMapping("/**")
                        .allowedOrigins(REACT_FRONT_END_URL, ANGULAR_FRONT_END_URL)
                        .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE");
            }
        };
    }
}
