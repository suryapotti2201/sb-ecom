package com.ecommerce.project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;

class SbEcomApplicationTest {

    @Test
    void mainDelegatesToSpringApplicationWithProvidedArguments() {
        String[] args = {"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            SbEcomApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(SbEcomApplication.class, args));
        }
    }

    @Test
    void mainDelegatesToSpringApplicationWhenArgumentsAreNull() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            SbEcomApplication.main(null);

            springApplication.verify(
                    () -> SpringApplication.run(SbEcomApplication.class, (String[]) null));
        }
    }
}
