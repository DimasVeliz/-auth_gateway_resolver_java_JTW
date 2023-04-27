package com.boosting.code.auth_gateway_resolver.services;

import com.boosting.code.auth_gateway_resolver.dtos.AuthServiceResponseDto;
import com.boosting.code.auth_gateway_resolver.dtos.AuthenticationDto;
import com.boosting.code.auth_gateway_resolver.dtos.RegisterDto;
import com.boosting.code.auth_gateway_resolver.entities.Role;
import com.boosting.code.auth_gateway_resolver.entities.Token;
import com.boosting.code.auth_gateway_resolver.entities.TokenType;
import com.boosting.code.auth_gateway_resolver.entities.User;
import com.boosting.code.auth_gateway_resolver.repositories.ITokenRepository;
import com.boosting.code.auth_gateway_resolver.repositories.IUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final IUserRepository repository;
    private final ITokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceResponseDto register(RegisterDto request) {
        HttpHeaders headers= new HttpHeaders();
        //TODO: add the refresh and access token to the headers object

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .uuid(UUID.randomUUID().toString())
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthServiceResponseDto.builder()
                .userEmail(user.getEmail())
                .headers(headers)
                .build();
    }

    public AuthServiceResponseDto authenticate(AuthenticationDto request) {
        HttpHeaders headers= new HttpHeaders();
        //TODO: add the refresh and access token to the headers object

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthServiceResponseDto.builder()
                .userEmail(user.getEmail())
                .headers(headers)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        HttpHeaders headers = new HttpHeaders();//TODO: possible bug, check
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthServiceResponseDto.builder()
                        .userEmail(user.getEmail())
                        .headers(headers)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
