package com.reliaquest.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author nikhilchavan
 */
@Configuration
@Getter
public class ApplicationConfiguration {

    @Value("${employee.base.uri}")
    public String employeeBaseUri;
}
