package com.boosting.code.auth_gateway_resolver.services;

import com.boosting.code.auth_gateway_resolver.dtos.AuthResponseDto;
import com.boosting.code.auth_gateway_resolver.dtos.RegisterDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationService {
    public AuthResponseDto register(RegisterDto registerDto) {
        return  null;
    }

    public AuthResponseDto authenticate(RegisterDto request) {
        return null;
    }

    public AuthResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
