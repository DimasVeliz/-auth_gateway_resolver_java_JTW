package com.boosting.code.auth_gateway_resolver.utilities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {
    @Value("${application.security.jwt.accessTokenCookieName}")
    private String accessTokenCookieName;

    @Value("${application.security.jwt.refreshTokenCookieName}")
    private String refreshTokenCookieName;

    public HttpCookie createAccessTokenCookie(String token, Long duration) {
        //TODO: Read about whether encrypting the token is an overkill, secure:FALSE over HTTP
        //String encryptedToken = SecurityCipher.encrypt(token);
        return ResponseCookie.from(accessTokenCookieName, token)
                .maxAge(duration)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .build();
    }

    public HttpCookie createRefreshTokenCookie(String token, Long duration) {
        //TODO: Read about whether encrypting the token is an overkill, secure:FALSE over HTTP
        //String encryptedToken = SecurityCipher.encrypt(token);
        return ResponseCookie.from(refreshTokenCookieName, token)
                .maxAge(duration)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .build();
    }

    public HttpCookie deleteAccessTokenCookie() {
        return ResponseCookie.from(accessTokenCookieName, "").maxAge(0).httpOnly(true).path("/").build();
    }
}
