package com.ecommerce.project.service;

import lombok.extern.slf4j.Slf4j;
import com.ecommerce.project.exception.EcommerceException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.RoleRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthUtil authUtil;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void grantRole(Long userId, String userRole) {
        log.info("AdminUserService => grantRole : In");

        AppRole roleName = switch (userRole) {
            case "ROLE_SELLER" -> AppRole.ROLE_SELLER;
            case "ROLE_ADMIN" -> AppRole.ROLE_ADMIN;
            default -> throw new EcommerceException("Invalid role. Allowed values: ROLE_ADMIN or ROLE_SELLER.");
        };

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow( () -> new ResourceNotFoundException("Role", "role", String.valueOf(roleName)));

        boolean alreadyAssigned = user.getRoles().stream()
                .anyMatch(existing -> existing.getRoleName() == roleName);

        if (alreadyAssigned) {
            log.info("AdminUserService => grantRole : Out");
            return;
        }

        user.getRoles().add(role);

        // Matches your current manual auditing implementation.
        user.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        user.setLastUpdatedOn(LocalDateTime.now());

        userRepository.save(user);
        log.info("AdminUserService => grantRole : Out");
    }
}