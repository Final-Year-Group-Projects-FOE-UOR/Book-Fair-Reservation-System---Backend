package com.bookfair.bookfairreservationsystembackend.config;

import com.bookfair.bookfairreservationsystembackend.services.JWTService;
import com.bookfair.bookfairreservationsystembackend.services.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter  extends OncePerRequestFilter {
    private  final JWTService jwtService;
    private  final MyUserDetailsService myUserDetailsService;

    @Value("${api.prefix}")
    private String apiPrefix;

    public JWTFilter(JWTService jwtService, MyUserDetailsService myUserDetailsService) {
        this.jwtService = jwtService;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return path.startsWith(apiPrefix+ "/users/register")
                || path.startsWith(apiPrefix+ "/users/login")
                || path.startsWith(apiPrefix+ "/users/register-moderator")
                || path.startsWith(apiPrefix+ "/login/oauth2")
                || path.startsWith(apiPrefix+ "/oauth2")
                || path.startsWith(apiPrefix+ "/users/google-success")
                || path.startsWith(apiPrefix + "/moderator/change-password-first-time");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        String header = request.getHeader("Authorization");
        String token = null;
        String email = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            email = jwtService.extractEmail(token);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(email);
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        System.out.println("Header: " + header);
        System.out.println("Extracted email: " + email);
        filterChain.doFilter(request, response);
    }

}
