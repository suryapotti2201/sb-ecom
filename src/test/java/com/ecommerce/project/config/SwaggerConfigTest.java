package com.ecommerce.project.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SwaggerConfigTest {

    private final SwaggerConfig swaggerConfig = new SwaggerConfig();

    @Test
    void customOpenAPI_returnsConfiguredOpenApiDefinition() {
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getSecurity());

        SecurityScheme bearerScheme = openAPI.getComponents()
                .getSecuritySchemes()
                .get("Bearer Authentication");

        SecurityRequirement securityRequirement = openAPI.getSecurity().get(0);
        Info info = openAPI.getInfo();
        Contact contact = info.getContact();

        assertAll(
                () -> assertNotNull(bearerScheme),
                () -> assertEquals(SecurityScheme.Type.HTTP, bearerScheme.getType()),
                () -> assertEquals("bearer", bearerScheme.getScheme()),
                () -> assertEquals("JWT", bearerScheme.getBearerFormat()),
                () -> assertEquals("JWT Bearer Token", bearerScheme.getDescription()),
                () -> assertTrue(securityRequirement.containsKey("Bearer Authentication")),
                () -> assertEquals("Spring Boot eCommerce Web Application", info.getTitle()),
                () -> assertEquals("1.0", info.getVersion()),
                () -> assertEquals("This is a Spring Boot Project for eCommerce", info.getDescription()),
                () -> assertNotNull(contact),
                () -> assertEquals("Surya Potti", contact.getName()),
                () -> assertEquals("suryapotti**@gmail.com", contact.getEmail())
        );
    }

    @Test
    void customOpenAPI_returnsIndependentConfiguredInstances() {
        OpenAPI first = swaggerConfig.customOpenAPI();
        OpenAPI second = swaggerConfig.customOpenAPI();

        assertAll(
                () -> assertNotNull(first),
                () -> assertNotNull(second),
                () -> assertTrue(first != second),
                () -> assertTrue(first.getComponents() != second.getComponents()),
                () -> assertEquals(
                        first.getComponents().getSecuritySchemes().keySet(),
                        second.getComponents().getSecuritySchemes().keySet()
                ),
                () -> assertEquals(
                        first.getInfo().getTitle(),
                        second.getInfo().getTitle()
                ),
                () -> assertEquals(
                        first.getSecurity().size(),
                        second.getSecurity().size()
                )
        );
    }
}
