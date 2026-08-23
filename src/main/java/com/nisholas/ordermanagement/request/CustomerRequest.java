package com.nisholas.ordermanagement.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(

        @NotNull
        String name,

        @NotNull
        @Email
        String email,

        @NotNull
        String phone,

        
        boolean active) {
}
