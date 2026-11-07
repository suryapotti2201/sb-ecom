package com.ecommerce.project.controller;

import lombok.extern.slf4j.Slf4j;
import com.ecommerce.project.service.AdminUserService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PutMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<Void> grantRole(
            @PathVariable @Positive(message = "User ID must be greater than zero") Long userId,
            @PathVariable String roleName) {
        log.info("AdminUserController => grantRole : In");

        adminUserService.grantRole(userId, roleName);
        log.info("AdminUserController => grantRole : Out");
        return ResponseEntity.noContent().build();
    }
}