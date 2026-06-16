package com.banking.platform.users.dto;

public record TokenResponse(
        String accessToken , String tokenType , long expiresInSeconds
) {
}
