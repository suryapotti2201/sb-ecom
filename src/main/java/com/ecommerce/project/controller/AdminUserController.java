package com.ecommerce.project.controller;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.service.AdminUserService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PutMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<Void> grantRole(
            @PathVariable @Positive(message = "User ID must be greater than zero") Long userId,
            @PathVariable AppRole roleName) {

        adminUserService.grantRole(userId, roleName);
        return ResponseEntity.noContent().build();
    }
}