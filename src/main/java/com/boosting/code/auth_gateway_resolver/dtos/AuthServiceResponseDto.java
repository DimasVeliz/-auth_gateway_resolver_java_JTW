package com.boosting.code.auth_gateway_resolver.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthServiceResponseDto {
    String token;
    String refreshToken;
    UserDto userDto;
    HttpHeaders headers;
}
