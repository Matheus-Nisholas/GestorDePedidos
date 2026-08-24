package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.entity.Role;
import com.nisholas.ordermanagement.entity.User;
import com.nisholas.ordermanagement.exception.InvalidCredentialsException;
import com.nisholas.ordermanagement.repository.UserRepository;
import com.nisholas.ordermanagement.request.LoginRequest;
import com.nisholas.ordermanagement.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = User.builder()
                .id(1L)
                .email("teste@email.com")
                .password("hashed-password")
                .role(Role.USER)
                .active(true)
                .build();
    }

    @Test
    void shouldLoginAndReturnJwtWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("teste@email.com", "12345678");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches(request.password(), activeUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(activeUser)).thenReturn("jwt-token");
        when(jwtService.getExpirationHours()).thenReturn(2L);

        LoginResponse response = authService.login(request);

        assertEquals("jwt-token", response.token());
        assertEquals("Bearer", response.type());
        assertEquals(2L, response.expiresInHours());
        assertEquals("teste@email.com", response.user().email());

        verify(jwtService).generateToken(activeUser);
    }

    @Test
    void shouldRejectLoginWhenEmailDoesNotExist() {
        LoginRequest request = new LoginRequest("naoexiste@email.com", "12345678");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void shouldRejectLoginWhenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest("teste@email.com", "senha-errada");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches(request.password(), activeUser.getPassword())).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void shouldRejectLoginWhenUserIsInactive() {
        activeUser.setActive(false);
        LoginRequest request = new LoginRequest("teste@email.com", "12345678");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(activeUser));

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(any());
    }
}
