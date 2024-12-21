package org.spring.jwt.service;

import io.jsonwebtoken.lang.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.jwt.dto.AuthRequest;
import org.spring.jwt.dto.AuthResponse;
import org.spring.jwt.dto.RegistrationRequest;
import org.spring.jwt.entity.Role;
import org.spring.jwt.entity.User;
import org.spring.jwt.mapper.UserMapper;
import org.spring.jwt.repository.RoleRepository;
import org.spring.jwt.repository.UserRepository;
import org.spring.jwt.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Value("${failed.attempts}")
    private int MAX_FAILED_ATTEMPTS;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    @Transactional
    public void register(RegistrationRequest registrationRequest) {
        Set<Role> roles = registrationRequest.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        User user = userMapper.toUser(registrationRequest, passwordEncoder, roles);

        userRepository.save(user);
    }

    public void unlockAccount(String username) {
        userRepository.unlockUserAccount(username);
        log.info("User {} account unlocked", username);
    }

    public AuthResponse auth(AuthRequest authRequest) {
        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + authRequest.getUsername()));

        if(!user.getIsAccountNonLocked()) {
            throw new AccessDeniedException("Account is locked. Contact administrator.");
        }

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            handleFailedLoginAttempt(user);
            return new AuthResponse("Invalid password or account locked.", Strings.EMPTY);
        }

        resetFailedAttempts(user);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getRoles());

        log.info("Login successful for user: {}", user.getUsername());
        return new AuthResponse("Login successful.", token);
    }

    private void resetFailedAttempts(User user) {
        if (user.getFailedAttempts() > 0) {
            userRepository.incrementFailedLoginAttempts(user.getUsername(), 0);
            log.info("Failed attempts reset for user {}", user.getUsername());
        }
    }

    private void handleFailedLoginAttempt(User user) {
        int newAttempts = user.getFailedAttempts() + 1;
        if (newAttempts >= MAX_FAILED_ATTEMPTS) {
            userRepository.lockUserAccount(user.getUsername());
            log.warn("User {} is locked due to {} failed attempts", user.getUsername(), MAX_FAILED_ATTEMPTS);
        } else {
            userRepository.incrementFailedLoginAttempts(user.getUsername(), newAttempts);
            log.warn("Failed login attempt for user {}. Attempt {}/{}", user.getUsername(), newAttempts, MAX_FAILED_ATTEMPTS);
        }
    }
}
