package com.boosting.code.auth_gateway_resolver.controllers;

import com.boosting.code.auth_gateway_resolver.dtos.AuthResponseDto;
import com.boosting.code.auth_gateway_resolver.dtos.AuthenticationDto;
import com.boosting.code.auth_gateway_resolver.dtos.RegisterDto;
import com.boosting.code.auth_gateway_resolver.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterDto registerDto){
        return ResponseEntity.ok(authenticationService.register(registerDto));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticate(
            @RequestBody AuthenticationDto authenticationDto
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationDto));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }


}
