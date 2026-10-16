package com.ecommerce.project.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternalWhenNoJwtTokenContinuesFilterChainWithoutAuthentication()
            throws ServletException, IOException {
        when(jwtUtils.getJwtFromHeader(request)).thenReturn(null);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils).getJwtFromHeader(request);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilterInternalWhenJwtTokenIsInvalidContinuesFilterChainWithoutAuthentication()
            throws ServletException, IOException {
        String token = "invalid-token";
        when(jwtUtils.getJwtFromHeader(request)).thenReturn(token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils).getJwtFromHeader(request);
        verify(jwtUtils).validateJwtToken(token);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilterInternalWhenJwtTokenIsValidSetsAuthenticationAndContinuesFilterChain()
            throws ServletException, IOException {
        String token = "valid-token";
        String username = "user@example.com";

        when(jwtUtils.getJwtFromHeader(request)).thenReturn(token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromJwtToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        authTokenFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertSame(userDetails, authentication.getPrincipal());
        assertNull(authentication.getCredentials());
        assertEquals(Collections.emptyList(), authentication.getAuthorities());
        assertNotNull(authentication.getDetails());

        verify(jwtUtils).getJwtFromHeader(request);
        verify(jwtUtils).validateJwtToken(token);
        verify(jwtUtils).getUsernameFromJwtToken(token);
        verify(userDetailsService).loadUserByUsername(username);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalWhenAuthenticationProcessingThrowsContinuesFilterChain()
            throws ServletException, IOException {
        RuntimeException failure = new RuntimeException("JWT processing failure");
        when(jwtUtils.getJwtFromHeader(request)).thenThrow(failure);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtils).getJwtFromHeader(request);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilterInternalWhenUserDetailsLoadingThrowsContinuesFilterChain()
            throws ServletException, IOException {
        String token = "valid-token";
        String username = "user@example.com";
        RuntimeException failure = new RuntimeException("User lookup failure");

        when(jwtUtils.getJwtFromHeader(request)).thenReturn(token);
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromJwtToken(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenThrow(failure);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService).loadUserByUsername(username);
        verify(filterChain).doFilter(request, response);
    }
}
