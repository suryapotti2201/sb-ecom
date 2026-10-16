package com.ecommerce.project.service;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.RoleRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.util.AuthUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private User user;

    @Mock
    private User loggedInUser;

    @Mock
    private Role role;

    @InjectMocks
    private AdminUserService adminUserService;

    private final Long userId = 42L;

    @Test
    void grantRole_assignsRoleAuditsUserAndSavesUser() {
        AppRole roleName = AppRole.ROLE_ADMIN;
        Set<Role> roles = new HashSet<>();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName(roleName))
                .thenReturn(Optional.of(role));
        when(user.getRoles()).thenReturn(roles);
        when(authUtil.loggedInUser()).thenReturn(loggedInUser);
        when(loggedInUser.getUserName()).thenReturn("admin-user");

        adminUserService.grantRole(userId, roleName);

        assertEquals(1, roles.size());
        assertTrue(roles.contains(role));

        verify(user).setLastUpdatedBy("admin-user");
        verify(user).setLastUpdatedOn(any(LocalDateTime.class));
        verify(userRepository).save(user);
    }

    @Test
    void grantRole_assignsSellerRole() {
        AppRole roleName = AppRole.ROLE_SELLER;
        Set<Role> roles = new HashSet<>();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName(roleName))
                .thenReturn(Optional.of(role));
        when(user.getRoles()).thenReturn(roles);
        when(authUtil.loggedInUser()).thenReturn(loggedInUser);
        when(loggedInUser.getUserName()).thenReturn("admin-user");

        adminUserService.grantRole(userId, roleName);

        assertEquals(Set.of(role), roles);

        verify(user).setLastUpdatedBy("admin-user");
        verify(user).setLastUpdatedOn(any(LocalDateTime.class));
        verify(userRepository).save(user);
    }

    @Test
    void grantRole_rejectsRoleThatIsNotAdminOrSeller() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> adminUserService.grantRole(userId, AppRole.ROLE_USER)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(
                "Only ROLE_ADMIN or ROLE_SELLER can be granted here",
                exception.getReason()
        );

        verifyNoInteractions(userRepository, roleRepository, authUtil);
    }

    @Test
    void grantRole_throwsNotFoundWhenUserDoesNotExist() {
        AppRole roleName = AppRole.ROLE_ADMIN;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> adminUserService.grantRole(userId, roleName)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found", exception.getReason());

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(roleRepository, authUtil);
    }

    @Test
    void grantRole_throwsWhenRoleIsMissingFromDatabase() {
        AppRole roleName = AppRole.ROLE_SELLER;

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName(roleName))
                .thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> adminUserService.grantRole(userId, roleName)
        );

        assertEquals(
                "Role is missing from the database: " + roleName,
                exception.getMessage()
        );

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(authUtil);
    }

    @Test
    void grantRole_doesNothingWhenUserAlreadyHasRole() {
        AppRole roleName = AppRole.ROLE_ADMIN;
        Set<Role> roles = new HashSet<>(Set.of(role));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName(roleName))
                .thenReturn(Optional.of(role));
        when(user.getRoles()).thenReturn(roles);
        when(role.getRoleName()).thenReturn(roleName);

        adminUserService.grantRole(userId, roleName);

        assertEquals(Set.of(role), roles);

        verify(user, never()).setLastUpdatedBy(any());
        verify(user, never()).setLastUpdatedOn(any());
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(authUtil);
    }
}