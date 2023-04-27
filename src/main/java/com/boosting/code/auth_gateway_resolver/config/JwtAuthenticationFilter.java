package com.boosting.code.auth_gateway_resolver.config;

import com.boosting.code.auth_gateway_resolver.repositories.ITokenRepository;
import com.boosting.code.auth_gateway_resolver.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ITokenRepository tokenRepository;

    @Value("${application.security.jwt.accessTokenCookieName}")
    private String accessTokenCookieName;

    @Value("${application.security.jwt.refreshTokenCookieName}")
    private String refreshTokenCookieName;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }


        //DONE: Added support for token authentication coming from a cookie or Bearer Header
        final String jwt = extractJwtToken(request);
        final String userEmail;
        if (jwt == null ) {
            filterChain.doFilter(request, response);
            return;
        }



        userEmail = jwtService.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtToken(HttpServletRequest request) {

        String jwt_cookie = getJwtToken(request, true);
        String jwt_bearer = getJwtToken(request, false);
        if(StringUtils.hasText(jwt_cookie)) return jwt_cookie;
        if(StringUtils.hasText(jwt_bearer)) return jwt_bearer;
        return null;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String accessToken = bearerToken.substring(7);
            if (accessToken == null) return null;

            return accessToken;
            //return SecurityCipher.decrypt(accessToken);
        }
        return null;
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (jakarta.servlet.http.Cookie cookie : cookies) {
            if (accessTokenCookieName.equals(cookie.getName())) {
                String accessToken = cookie.getValue();
                if (accessToken == null) return null;
                return accessToken;

                //return SecurityCipher.decrypt(accessToken);
            }
        }
        return null;
    }

    private String getJwtToken(HttpServletRequest request, boolean fromCookie) {
        if (fromCookie) return getJwtFromCookie(request);

        return getJwtFromRequest(request);
    }
}
