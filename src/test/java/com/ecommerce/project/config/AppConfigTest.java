package com.ecommerce.project.config;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppConfigTest {

    private final AppConfig appConfig = new AppConfig();

    @Test
    void modelMapper_returnsNonNullModelMapperInstance() {
        ModelMapper modelMapper = appConfig.modelMapper();

        assertNotNull(modelMapper);
        assertTrue(modelMapper instanceof ModelMapper);
    }

    @Test
    void modelMapper_returnsNewInstanceOnEachInvocation() {
        ModelMapper first = appConfig.modelMapper();
        ModelMapper second = appConfig.modelMapper();

        assertNotSame(first, second);
    }
}
