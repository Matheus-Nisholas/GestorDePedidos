package com.nisholas.ordermanagement.response;

import lombok.Builder;

@Builder
public record CustomerResponse(Long id, String name) {
}
