package com.ecommerce.project.controller;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.RoleRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignupRequest;
import com.ecommerce.project.security.response.MessageResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.service.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticateUserReturnsUserInfoForValidCredentials() {
        LoginRequest loginRequest = mock(LoginRequest.class);
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

        when(loginRequest.getUsername()).thenReturn("customer");
        when(loginRequest.getPassword()).thenReturn("password");
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(7L);
        when(userDetails.getUsername()).thenReturn("customer");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .when(authentication)
                .getAuthorities();
        when(jwtUtils.generateTokenFromUsername(userDetails)).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(UserInfoResponse.class, response.getBody());
        assertSame(authentication, SecurityContextHolder.getContext().getAuthentication());

        ArgumentCaptor<Authentication> authenticationCaptor =
                ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(authenticationCaptor.capture());
        assertInstanceOf(
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken.class,
                authenticationCaptor.getValue());
        assertEquals("customer", authenticationCaptor.getValue().getName());
        verify(jwtUtils).generateTokenFromUsername(userDetails);
    }

    @Test
    void authenticateUserReturnsUnauthorizedForBadCredentials() {
        LoginRequest loginRequest = mock(LoginRequest.class);
        AuthenticationException authenticationException = mock(AuthenticationException.class);

        when(loginRequest.getUsername()).thenReturn("customer");
        when(loginRequest.getPassword()).thenReturn("wrong-password");
        when(authenticationManager.authenticate(any())).thenThrow(authenticationException);

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Bad credentials", body.get("message"));
        assertEquals(false, body.get("status"));

        verifyNoInteractions(jwtUtils);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void registerUserReturnsBadRequestWhenUsernameAlreadyExists() {
        SignupRequest signupRequest = mock(SignupRequest.class);

        when(signupRequest.getUserName()).thenReturn("existing-user");
        when(userRepository.existsByUserName("existing-user")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(MessageResponse.class, response.getBody());

        verify(userRepository, never()).existsByEmailId(anyString());
        verifyNoInteractions(passwordEncoder, roleRepository);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUserReturnsBadRequestWhenEmailAlreadyExists() {
        SignupRequest signupRequest = mock(SignupRequest.class);

        when(signupRequest.getUserName()).thenReturn("new-user");
        when(signupRequest.getEmailId()).thenReturn("existing@example.com");
        when(userRepository.existsByUserName("new-user")).thenReturn(false);
        when(userRepository.existsByEmailId("existing@example.com")).thenReturn(true);

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(MessageResponse.class, response.getBody());

        verifyNoInteractions(passwordEncoder, roleRepository);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUserSavesUserWithEncodedPasswordAndUserRole() {
        SignupRequest signupRequest = mock(SignupRequest.class);
        Role userRole = mock(Role.class);

        when(signupRequest.getUserName()).thenReturn("new-user");
        when(signupRequest.getEmailId()).thenReturn("new@example.com");
        when(signupRequest.getPassword()).thenReturn("plain-password");
        when(userRepository.existsByUserName("new-user")).thenReturn(false);
        when(userRepository.existsByEmailId("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(roleRepository.findByRoleName(AppRole.ROLE_USER)).thenReturn(Optional.of(userRole));

        ResponseEntity<?> response = authController.registerUser(signupRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(MessageResponse.class, response.getBody());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertNotNull(userCaptor.getValue());

        verify(passwordEncoder).encode("plain-password");
        verify(roleRepository).findByRoleName(AppRole.ROLE_USER);
    }

    @Test
    void registerUserThrowsWhenUserRoleIsMissing() {
        SignupRequest signupRequest = mock(SignupRequest.class);

        when(signupRequest.getUserName()).thenReturn("new-user");
        when(signupRequest.getEmailId()).thenReturn("new@example.com");
        when(signupRequest.getPassword()).thenReturn("plain-password");
        when(userRepository.existsByUserName("new-user")).thenReturn(false);
        when(userRepository.existsByEmailId("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(roleRepository.findByRoleName(AppRole.ROLE_USER)).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> authController.registerUser(signupRequest));

        assertEquals("ROLE_USER is missing from the database", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void currentUserNameReturnsAuthenticatedUsername() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("customer");

        ResponseEntity<String> response = authController.currentUserName(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("customer", response.getBody());
    }

    @Test
    void currentUserNameReturnsEmptyStringWhenAuthenticationIsNull() {
        ResponseEntity<String> response = authController.currentUserName(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("", response.getBody());
    }
}
