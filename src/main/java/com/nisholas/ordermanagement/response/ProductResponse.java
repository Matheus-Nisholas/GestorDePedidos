package com.nisholas.ordermanagement.response;

import lombok.Builder;

@Builder
public record ProductResponse(
        Long id,
        String name
) {
}
