package com.ecommerce.project.repository;

import com.ecommerce.project.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private User user;

    @Test
    void findByUserNameReturnsUserWhenUserExists() {
        String username = "jane.doe";
        Optional<User> expected = Optional.of(user);

        when(userRepository.findByUserName(username)).thenReturn(expected);

        Optional<User> actual = userRepository.findByUserName(username);

        assertSame(expected, actual);
        verify(userRepository).findByUserName(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByUserNameReturnsEmptyWhenUserDoesNotExist() {
        String username = "unknown.user";
        Optional<User> expected = Optional.empty();

        when(userRepository.findByUserName(username)).thenReturn(expected);

        Optional<User> actual = userRepository.findByUserName(username);

        assertTrue(actual.isEmpty());
        verify(userRepository).findByUserName(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void existsByUserNameReturnsTrueWhenUsernameExists() {
        String username = "jane.doe";

        when(userRepository.existsByUserName(username)).thenReturn(true);

        boolean actual = userRepository.existsByUserName(username);

        assertTrue(actual);
        verify(userRepository).existsByUserName(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void existsByUserNameReturnsFalseWhenUsernameDoesNotExist() {
        String username = "unknown.user";

        when(userRepository.existsByUserName(username)).thenReturn(false);

        boolean actual = userRepository.existsByUserName(username);

        assertFalse(actual);
        verify(userRepository).existsByUserName(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void existsByEmailIdReturnsTrueWhenEmailExists() {
        String emailId = "jane.doe@example.com";

        when(userRepository.existsByEmailId(emailId)).thenReturn(true);

        boolean actual = userRepository.existsByEmailId(emailId);

        assertTrue(actual);
        verify(userRepository).existsByEmailId(emailId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void existsByEmailIdReturnsFalseWhenEmailDoesNotExist() {
        String emailId = "unknown@example.com";

        when(userRepository.existsByEmailId(emailId)).thenReturn(false);

        boolean actual = userRepository.existsByEmailId(emailId);

        assertFalse(actual);
        verify(userRepository).existsByEmailId(emailId);
        verifyNoMoreInteractions(userRepository);
    }
}
