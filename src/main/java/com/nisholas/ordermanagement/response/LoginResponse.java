package com.nisholas.ordermanagement.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        String token,
        String type,
        long expiresInHours,
        UserResponse user
) {
}
