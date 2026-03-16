package com.laurefindel.finance.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Finance API",
        version = "v1",
        description = "API for users, roles, currencies, accounts and financial operations",
        contact = @Contact(name = "Finance Team"),
        license = @License(name = "Internal")
    )
)
public class OpenApiConfig {
}
