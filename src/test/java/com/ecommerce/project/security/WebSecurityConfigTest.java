package com.ecommerce.project.security;

import com.ecommerce.project.security.jwt.AuthEntryPoint;
import com.ecommerce.project.security.jwt.AuthTokenFilter;
import com.ecommerce.project.security.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSecurityConfigTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthEntryPoint unauthorizedHandler;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock(answer = Answers.RETURNS_SELF)
    private HttpSecurity httpSecurity;

    @Mock
    private DefaultSecurityFilterChain securityFilterChain;

    @InjectMocks
    private WebSecurityConfig webSecurityConfig;

    @Test
    void authenticationJwtTokenFilterReturnsNewFilter() {
        AuthTokenFilter first = webSecurityConfig.authenticationJwtTokenFilter();
        AuthTokenFilter second = webSecurityConfig.authenticationJwtTokenFilter();

        assertNotNull(first);
        assertNotNull(second);
        assertFalse(first == second);
    }

    @Test
    void authenticationProviderAuthenticatesUserWithConfiguredUserDetailsService() {
        String encodedPassword = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                .encode("secret");

        when(userDetailsService.loadUserByUsername("alice"))
                .thenReturn(User.withUsername("alice")
                        .password(encodedPassword)
                        .roles("USER")
                        .build());

        DaoAuthenticationProvider provider = webSecurityConfig.authenticationProvider();

        Authentication authentication = provider.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated("alice", "secret"));

        assertTrue(authentication.isAuthenticated());
        assertSame("alice", authentication.getName());
        verify(userDetailsService).loadUserByUsername("alice");
    }

    @Test
    void authenticationProviderRejectsInvalidPassword() {
        String encodedPassword = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
                .encode("secret");

        when(userDetailsService.loadUserByUsername("alice"))
                .thenReturn(User.withUsername("alice")
                        .password(encodedPassword)
                        .roles("USER")
                        .build());

        DaoAuthenticationProvider provider = webSecurityConfig.authenticationProvider();

        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.security.authentication.BadCredentialsException.class,
                () -> provider.authenticate(
                        UsernamePasswordAuthenticationToken.unauthenticated("alice", "wrong-password")));
    }

    @Test
    void filterChainBuildsSecurityFilterChainAndConfiguresHttpSecurity()
            throws Exception {

        when(httpSecurity.build()).thenReturn(securityFilterChain);

        SecurityFilterChain result =
                webSecurityConfig.filterChain(httpSecurity);

        assertSame(securityFilterChain, result);

        verify(httpSecurity).csrf(any());
        verify(httpSecurity).exceptionHandling(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).authenticationProvider(
                any(DaoAuthenticationProvider.class)
        );
        verify(httpSecurity).addFilterBefore(
                any(AuthTokenFilter.class),
                eq(UsernamePasswordAuthenticationFilter.class)
        );
        verify(httpSecurity).build();
    }

    @Test
    void webSecurityCustomizerCanCustomizeWebSecurity() {
        WebSecurity webSecurity = org.mockito.Mockito.mock(
                WebSecurity.class, Answers.RETURNS_DEEP_STUBS);

        webSecurityConfig.webSecurityCustomizer().customize(webSecurity);

        verify(webSecurity).ignoring();
    }

    @Test
    void passwordEncoderEncodesAndVerifiesPasswords() {
        PasswordEncoder passwordEncoder = webSecurityConfig.passwordEncoder();

        String encodedPassword = passwordEncoder.encode("secret");

        assertNotNull(encodedPassword);
        assertTrue(passwordEncoder.matches("secret", encodedPassword));
        assertFalse(passwordEncoder.matches("wrong-password", encodedPassword));
    }

    @Test
    void authenticationManagerDelegatesToAuthenticationConfiguration() throws Exception {
        AuthenticationManager authenticationManager = org.mockito.Mockito.mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager result =
                webSecurityConfig.authenticationManager(authenticationConfiguration);

        assertSame(authenticationManager, result);
        verify(authenticationConfiguration).getAuthenticationManager();
    }
}
