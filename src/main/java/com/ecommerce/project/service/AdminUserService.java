package com.ecommerce.project.service;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.RoleRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthUtil authUtil;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void grantRole(Long userId, AppRole roleName) {

        if (roleName != AppRole.ROLE_ADMIN
                && roleName != AppRole.ROLE_SELLER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only ROLE_ADMIN or ROLE_SELLER can be granted here"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new IllegalStateException(
                        "Role is missing from the database: " + roleName
                ));

        boolean alreadyAssigned = user.getRoles().stream()
                .anyMatch(existing -> existing.getRoleName() == roleName);

        if (alreadyAssigned) {
            return;
        }

        user.getRoles().add(role);

        // Matches your current manual auditing implementation.
        user.setLastUpdatedBy(authUtil.loggedInUser().getUserName());
        user.setLastUpdatedOn(LocalDateTime.now());

        userRepository.save(user);
    }
}