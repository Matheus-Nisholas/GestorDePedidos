package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.Mapper.UserMapper;
import com.nisholas.ordermanagement.entity.Role;
import com.nisholas.ordermanagement.entity.User;
import com.nisholas.ordermanagement.exception.EmailAlreadyExistsException;
import com.nisholas.ordermanagement.exception.InvalidCredentialsException;
import com.nisholas.ordermanagement.repository.UserRepository;
import com.nisholas.ordermanagement.request.LoginRequest;
import com.nisholas.ordermanagement.request.RegisterRequest;
import com.nisholas.ordermanagement.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered: " + request.email()
            );
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        return UserMapper.toUserResponse(savedUser);
    }

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid email or password"
                ));

        if (!user.isActive() ||
                !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        return UserMapper.toUserResponse(user);
    }
}
