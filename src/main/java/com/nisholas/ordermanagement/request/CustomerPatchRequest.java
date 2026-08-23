package com.nisholas.ordermanagement.request;

import jakarta.validation.constraints.Email;

public record CustomerPatchRequest(
        String name,
        @Email(message = "Email must be valid")
        String email,
        String phone,
        Boolean active
) {
}