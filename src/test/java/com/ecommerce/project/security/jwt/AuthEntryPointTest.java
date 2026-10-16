package com.ecommerce.project.security.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthEntryPointTest {

    private final AuthEntryPoint authEntryPoint = new AuthEntryPoint();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authenticationException;

    @Test
    void commenceWritesUnauthorizedResponseAsJson() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = servletOutputStream(output);

        when(authenticationException.getMessage()).thenReturn("Authentication is required");
        when(request.getServletPath()).thenReturn("/api/products");
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        authEntryPoint.commence(request, response, authenticationException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).getOutputStream();

        JsonNode body = new ObjectMapper().readTree(output.toByteArray());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, body.get("status").asInt());
        assertEquals("Unauthorized", body.get("error").asText());
        assertEquals("Authentication is required", body.get("message").asText());
        assertEquals("/api/products", body.get("path").asText());
    }

    @Test
    void commenceIncludesNullMessageAndPathInJsonResponse() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = servletOutputStream(output);

        when(authenticationException.getMessage()).thenReturn(null);
        when(request.getServletPath()).thenReturn(null);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        authEntryPoint.commence(request, response, authenticationException);

        JsonNode body = new ObjectMapper().readTree(output.toByteArray());
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, body.get("status").asInt());
        assertEquals("Unauthorized", body.get("error").asText());
        assertEquals(true, body.get("message").isNull());
        assertEquals(true, body.get("path").isNull());
    }

    @Test
    void commencePropagatesIOExceptionFromResponseOutputStream() throws Exception {
        IOException expectedException = new IOException("Unable to access response output stream");

        when(authenticationException.getMessage()).thenReturn("Authentication failed");
        when(request.getServletPath()).thenReturn("/secure");
        when(response.getOutputStream()).thenThrow(expectedException);

        IOException actualException = assertThrows(
                IOException.class,
                () -> authEntryPoint.commence(request, response, authenticationException)
        );

        assertEquals(expectedException, actualException);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private ServletOutputStream servletOutputStream(ByteArrayOutputStream output) {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
            }

            @Override
            public void write(int value) {
                output.write(value);
            }
        };
    }
}
