package org.spring.jwt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UnlockUserRequest {
    @NotBlank(message = "Username cannot be blank")
    private String username;
}
