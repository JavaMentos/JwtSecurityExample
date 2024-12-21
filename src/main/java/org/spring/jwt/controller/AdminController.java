package org.spring.jwt.controller;

import lombok.RequiredArgsConstructor;
import org.spring.jwt.dto.UnlockUserRequest;
import org.spring.jwt.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok("Data for Admin");
    }

    @PostMapping("/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> unlockUserAccount(@RequestBody UnlockUserRequest user) {
        userService.unlockAccount(user.getUsername());
        return ResponseEntity.ok("User " + user.getUsername() + " is unlocked");
    }

}