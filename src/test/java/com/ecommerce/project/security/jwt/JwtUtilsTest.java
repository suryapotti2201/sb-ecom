package com.ecommerce.project.security.jwt;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    private static final String SECRET = Base64.getEncoder().encodeToString(
            "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8)
    );
    private static final long EXPIRATION_MILLISECONDS = 60_000L;

    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", EXPIRATION_MILLISECONDS);
    }

    @Test
    void getJwtFromHeaderReturnsTokenWhenAuthorizationHeaderUsesBearerScheme() {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-value");

        String result = jwtUtils.getJwtFromHeader(request);

        assertEquals("token-value", result);
        verify(request).getHeader("Authorization");
    }

    @Test
    void getJwtFromHeaderReturnsNullWhenHeaderIsMissing() {
        when(request.getHeader("Authorization")).thenReturn(null);

        assertNull(jwtUtils.getJwtFromHeader(request));
    }

    @Test
    void getJwtFromHeaderReturnsNullWhenHeaderDoesNotUseBearerScheme() {
        when(request.getHeader("Authorization")).thenReturn("Basic credentials");

        assertNull(jwtUtils.getJwtFromHeader(request));
    }

    @Test
    void getJwtFromHeaderReturnsEmptyTokenForBearerHeaderWithoutToken() {
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        assertEquals("", jwtUtils.getJwtFromHeader(request));
    }

    @Test
    void keyCreatesKeyFromConfiguredBase64Secret() {
        Key key = jwtUtils.key();

        assertArrayEquals(
                "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8),
                key.getEncoded()
        );
    }

    @Test
    void generateTokenFromUsernameCreatesTokenContainingUsername() {
        when(userDetails.getUsername()).thenReturn("alice");

        String token = jwtUtils.generateTokenFromUsername(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals("alice", jwtUtils.getUsernameFromJwtToken(token));
        verify(userDetails).getUsername();
    }

    @Test
    void getUsernameFromJwtTokenReturnsSubjectFromValidToken() {
        when(userDetails.getUsername()).thenReturn("alice");
        String token = jwtUtils.generateTokenFromUsername(userDetails);

        assertEquals("alice", jwtUtils.getUsernameFromJwtToken(token));
    }

    @Test
    void validateJwtTokenReturnsTrueForValidToken() {
        when(userDetails.getUsername()).thenReturn("alice");
        String token = jwtUtils.generateTokenFromUsername(userDetails);

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtTokenReturnsFalseForMalformedToken() {
        assertFalse(jwtUtils.validateJwtToken("not-a-jwt"));
    }

    @Test
    void validateJwtTokenReturnsFalseForEmptyToken() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    @Test
    void validateJwtTokenReturnsFalseForExpiredToken() {
        String expiredToken = Jwts.builder()
                .subject("alice")
                .issuedAt(new Date(System.currentTimeMillis() - 10_000L))
                .expiration(new Date(System.currentTimeMillis() - 1_000L))
                .signWith(jwtUtils.key())
                .compact();

        assertFalse(jwtUtils.validateJwtToken(expiredToken));
    }
}
