package com.boosting.code.auth_gateway_resolver.services;

import com.boosting.code.auth_gateway_resolver.repositories.ITokenRepository;
import com.boosting.code.auth_gateway_resolver.utilities.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final ITokenRepository tokenRepository;
  private final CookieUtils cookieUtils;

  @Override
  public void logout(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication
  ) {

    //destroying the jwt cookie
    removeAccessTokenCookie(response);
    removeRefreshTokenCookie(response);


    //logging off the user if it used jwt as authentication method via bearer
    final String authHeader = request.getHeader("Authorization");
    final String jwt;

    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    jwt = authHeader.substring(7);
    var storedToken = tokenRepository.findByToken(jwt)
            .orElse(null);
    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      tokenRepository.save(storedToken);
      SecurityContextHolder.clearContext();
    }
  }

  private void removeRefreshTokenCookie(HttpServletResponse response) {
    response.addHeader(HttpHeaders.SET_COOKIE, cookieUtils.deleteRefreshTokenCookie().toString());
  }

  private void removeAccessTokenCookie(HttpServletResponse response) {
    response.addHeader(HttpHeaders.SET_COOKIE, cookieUtils.deleteAccessTokenCookie().toString());
  }
}

