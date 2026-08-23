package com.nisholas.ordermanagement.request;

public record CustomerRequest(String name, String email, String phone, boolean active) {
}
