package com.ecommerce.project.security.service;

import lombok.extern.slf4j.Slf4j;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("UserDetailsServiceImpl => loadUserByUsername : In");
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for username : " + username));
        log.info("UserDetailsServiceImpl => loadUserByUsername : Out");
        return UserDetailsImpl.build(user);
    }
}
