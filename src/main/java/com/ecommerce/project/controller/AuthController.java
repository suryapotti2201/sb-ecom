package com.ecommerce.project.controller;

import lombok.extern.slf4j.Slf4j;
import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.RoleRepository;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.security.jwt.JwtUtils;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignupRequest;
import com.ecommerce.project.security.response.MessageResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import com.ecommerce.project.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        log.info("AuthController => authenticateUser : In");
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword())
            );
        }catch (AuthenticationException e){
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            log.info("AuthController => authenticateUser : Out");
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        UserInfoResponse loginResponse = new UserInfoResponse(userDetails.getId(), jwtToken,
                userDetails.getUsername(), roles);
        log.info("AuthController => authenticateUser : Out");
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        log.info("AuthController => registerUser : In");
        if(userRepository.existsByUserName(signupRequest.getUserName())){
            log.info("AuthController => registerUser : Out");
            return ResponseEntity.badRequest().body(
                    new MessageResponse("Error: Username is already taken!"));
        }
        if(userRepository.existsByEmailId(signupRequest.getEmailId())){
            log.info("AuthController => registerUser : Out");
            return ResponseEntity.badRequest().body(
                    new MessageResponse("Error: EmailId is already taken!"));
        }
        User user = new User(signupRequest.getUserName(), signupRequest.getEmailId(),
                passwordEncoder.encode(signupRequest.getPassword())
        );
        Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                .orElseThrow(() ->
                        new IllegalStateException("ROLE_USER is missing from the database"));

        user.setRoles(new HashSet<>(Set.of(userRole)));
        user.setCreatedBy(signupRequest.getUserName());
        user.setLastUpdatedBy(signupRequest.getUserName());
        user.setCreatedOn(LocalDateTime.now());
        user.setLastUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
        log.info("AuthController => registerUser : Out");
        return ResponseEntity.ok().body(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/username")
    public ResponseEntity<String> currentUserName(Authentication authentication){
        log.info("AuthController => currentUserName : In");
        String username = "";
        if (authentication != null) {
            username = authentication.getName();
        }
        log.info("AuthController => currentUserName : Out");
        return new ResponseEntity<>(username, HttpStatus.OK);
    }
}
