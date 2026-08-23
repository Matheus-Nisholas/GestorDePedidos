package com.nisholas.ordermanagement.Mapper;

import com.nisholas.ordermanagement.entity.User;
import com.nisholas.ordermanagement.response.UserResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }
}
