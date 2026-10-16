package com.ecommerce.project.util;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUtilTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private User user;

    @InjectMocks
    private AuthUtil authUtil;

    @BeforeEach
    void setUpSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getName()).thenReturn("alice");
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loggedInUser_returnsUserFoundForAuthenticatedUsername() {
        when(userRepository.findByUserName("alice")).thenReturn(Optional.of(user));

        User result = authUtil.loggedInUser();

        assertSame(user, result);
        verify(authentication).getName();
        verify(userRepository).findByUserName("alice");
    }

    @Test
    void loggedInUser_throwsUsernameNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findByUserName("alice")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authUtil.loggedInUser()
        );

        assertEquals("User not Found!", exception.getMessage());
        verify(authentication).getName();
        verify(userRepository).findByUserName("alice");
    }

    @Test
    void loggedInUserId_returnsAuthenticatedUserId() {
        Long userId = 42L;
        when(userRepository.findByUserName("alice")).thenReturn(Optional.of(user));
        when(user.getUserId()).thenReturn(userId);

        Long result = authUtil.loggedInUserId();

        assertEquals(userId, result);
        verify(userRepository).findByUserName("alice");
        verify(user).getUserId();
    }

    @Test
    void loggedInUserEmailId_returnsAuthenticatedUserEmail() {
        String email = "alice@example.com";
        when(userRepository.findByUserName("alice")).thenReturn(Optional.of(user));
        when(user.getEmailId()).thenReturn(email);

        String result = authUtil.loggedInEmailId();

        assertEquals(email, result);
        verify(userRepository).findByUserName("alice");
        verify(user).getEmailId();
    }

    @Test
    void loggedInUserId_propagatesUserNotFoundException() {
        when(userRepository.findByUserName("alice")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authUtil.loggedInUserId());

        verify(userRepository).findByUserName("alice");
    }

    @Test
    void loggedInUserEmailId_propagatesUserNotFoundException() {
        when(userRepository.findByUserName("alice")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authUtil.loggedInEmailId());

        verify(userRepository).findByUserName("alice");
    }
}
