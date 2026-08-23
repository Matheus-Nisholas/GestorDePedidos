package com.nisholas.ordermanagement.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(

        @NotNull(message = "Name is required")
        String name,

        @NotNull(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotNull(message = "Phone is required")
        String phone,

        @NotNull(message = "Active status is required")
        boolean active) {
}
