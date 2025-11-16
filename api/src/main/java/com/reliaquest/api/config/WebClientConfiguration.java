package com.reliaquest.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author nikhilchavan
 */
@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {

    private final ApplicationConfiguration appConfig;

    @Bean
    public WebClient employeeServiceExternalClient() {
        return WebClient.builder().baseUrl(appConfig.getEmployeeBaseUri()).build();
    }
}
