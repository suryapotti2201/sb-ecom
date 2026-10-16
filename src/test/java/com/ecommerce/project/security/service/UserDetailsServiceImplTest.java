package com.ecommerce.project.security.service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsernameReturnsUserDetailsWhenUserExists() {
        String username = "jane.doe";
        Long userId = 42L;
        String email = "jane.doe@example.com";
        String password = "encoded-password";

        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));
        when(user.getUserId()).thenReturn(userId);
        when(user.getUserName()).thenReturn(username);
        when(user.getEmailId()).thenReturn(email);
        when(user.getPassword()).thenReturn(password);
        doReturn(Collections.emptySet()).when(user).getRoles();

        UserDetails result = userDetailsService.loadUserByUsername(username);

        UserDetailsImpl userDetails = assertInstanceOf(UserDetailsImpl.class, result);
        assertEquals(userId, userDetails.getId());
        assertEquals(username, userDetails.getUsername());
        assertEquals(email, userDetails.getEmailId());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(userRepository).findByUserName(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void loadUserByUsernameThrowsExceptionWhenUserDoesNotExist() {
        String username = "missing.user";

        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username)
        );

        assertEquals("User not found for username : " + username, exception.getMessage());
        verify(userRepository).findByUserName(username);
        verifyNoMoreInteractions(userRepository);
    }
}
