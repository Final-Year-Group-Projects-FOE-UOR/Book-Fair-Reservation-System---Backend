package com.bookfair.bookfairreservationsystembackend.config;

import com.bookfair.bookfairreservationsystembackend.services.auth.GoogleAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final GoogleAuthService googleAuthService;

    public OAuth2LoginSuccessHandler(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        var loginResponse = googleAuthService.hangleGoogleLogin(oAuth2User);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(loginResponse));
    }
}