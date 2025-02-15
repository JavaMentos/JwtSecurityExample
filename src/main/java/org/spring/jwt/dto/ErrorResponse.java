package org.spring.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
}
