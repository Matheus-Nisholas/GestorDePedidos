package com.nisholas.ordermanagement.controller;

import com.nisholas.ordermanagement.request.LoginRequest;
import com.nisholas.ordermanagement.request.RegisterRequest;
import com.nisholas.ordermanagement.response.UserResponse;
import com.nisholas.ordermanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de cadastro e autenticação de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Cadastrar usuário",
            description = "Cria um novo usuário com perfil USER e armazena a senha com BCrypt."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de cadastro inválidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Realizar login",
            description = "Valida e-mail e senha do usuário utilizando o hash BCrypt armazenado no banco. Nesta etapa ainda não gera JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credenciais válidas"),
            @ApiResponse(responseCode = "400", description = "Dados de login inválidos"),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha incorretos")
    })
    public ResponseEntity<UserResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
}
