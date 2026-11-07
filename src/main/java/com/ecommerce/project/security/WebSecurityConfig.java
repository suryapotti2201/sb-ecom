package com.ecommerce.project.security;

import lombok.extern.slf4j.Slf4j;
import com.ecommerce.project.security.jwt.AuthEntryPoint;
import com.ecommerce.project.security.jwt.AuthTokenFilter;
import com.ecommerce.project.security.service.UserDetailsServiceImpl;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPoint unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        log.info("WebSecurityConfig => authenticationProvider : In");
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        log.info("WebSecurityConfig => authenticationProvider : Out");
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("WebSecurityConfig => filterChain : In");
        http.csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests ->
                requests.dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api/admin/**", "/app/admin/**").hasRole("ADMIN")
                .requestMatchers("/app/seller/**").hasRole("SELLER")
                    .anyRequest().authenticated())
            .authenticationProvider(authenticationProvider())
                .addFilterBefore(authenticationJwtTokenFilter(),
                        UsernamePasswordAuthenticationFilter.class);
        log.info("WebSecurityConfig => filterChain : Out");
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web -> web.ignoring().requestMatchers("/v2/api-docs/",
                "/configuraton/ui", "/swagger-resources/**", "/configuration/security",
                "/swagger-ui.html", "/webjars/**"));
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}