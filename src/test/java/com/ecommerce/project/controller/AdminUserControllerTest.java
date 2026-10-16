package com.ecommerce.project.controller;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private AdminUserService adminUserService;

    @Test
    void grantRoleDelegatesToServiceAndReturnsNoContent() {
        AdminUserController controller = new AdminUserController(adminUserService);
        Long userId = 42L;
        AppRole role = AppRole.ROLE_USER;

        ResponseEntity<Void> response = controller.grantRole(userId, role);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(adminUserService).grantRole(userId, role);
        verifyNoMoreInteractions(adminUserService);
    }

    @Test
    void grantRolePropagatesServiceException() {
        AdminUserController controller = new AdminUserController(adminUserService);
        Long userId = 42L;
        AppRole role = AppRole.ROLE_USER;
        RuntimeException serviceException = new RuntimeException("Unable to grant role");

        doThrow(serviceException).when(adminUserService).grantRole(userId, role);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> controller.grantRole(userId, role)
        );

        assertEquals(serviceException, thrown);
        verify(adminUserService).grantRole(userId, role);
        verifyNoMoreInteractions(adminUserService);
    }
}
