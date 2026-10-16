package com.ecommerce.project.repository;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AppRole appRole;

    @Mock
    private Role role;

    @Test
    void findByRoleNameReturnsMatchingRoleWhenPresent() {
        Optional<Role> expected = Optional.of(role);
        when(roleRepository.findByRoleName(appRole)).thenReturn(expected);

        Optional<Role> result = roleRepository.findByRoleName(appRole);

        assertTrue(result.isPresent());
        assertSame(role, result.get());
        verify(roleRepository).findByRoleName(appRole);
    }

    @Test
    void findByRoleNameReturnsEmptyWhenNoRoleMatches() {
        when(roleRepository.findByRoleName(appRole)).thenReturn(Optional.empty());

        Optional<Role> result = roleRepository.findByRoleName(appRole);

        assertTrue(result.isEmpty());
        verify(roleRepository).findByRoleName(appRole);
    }

    @Test
    void findByRoleNameSupportsNullRoleName() {
        when(roleRepository.findByRoleName(null)).thenReturn(Optional.empty());

        Optional<Role> result = roleRepository.findByRoleName(null);

        assertTrue(result.isEmpty());
        verify(roleRepository).findByRoleName(null);
    }
}
