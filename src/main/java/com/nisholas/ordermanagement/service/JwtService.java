package com.nisholas.ordermanagement.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nisholas.ordermanagement.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final long expirationHours;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-hours}") long expirationHours) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationHours = expirationHours;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationHours, ChronoUnit.HOURS);

        return JWT.create()
                .withIssuer("gestao-pedidos-api")
                .withSubject(user.getEmail())
                .withClaim("userId", user.getId())
                .withClaim("role", user.getRole().name())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(algorithm);
    }

    public long getExpirationHours() {
        return expirationHours;
    }
}
