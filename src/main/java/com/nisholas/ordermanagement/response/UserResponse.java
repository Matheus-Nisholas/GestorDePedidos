package com.nisholas.ordermanagement.response;

import com.nisholas.ordermanagement.entity.Role;
import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String email,
        Role role,
        boolean active
) {
}
