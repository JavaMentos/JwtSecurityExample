package org.spring.jwt.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public String userAccess() {
        return "Data for users";
    }

    @GetMapping("/moderate")
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderateAccess() {
        return "Data for moderators";
    }

}
