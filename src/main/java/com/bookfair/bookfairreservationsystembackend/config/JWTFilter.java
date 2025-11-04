package com.bookfair.bookfairreservationsystembackend.config;

import com.bookfair.bookfairreservationsystembackend.services.JWTService;
import com.bookfair.bookfairreservationsystembackend.services.MyUserDetailsService;
import com.bookfair.bookfairreservationsystembackend.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public JWTFilter(JWTService jwtService, MyUserDetailsService myUserDetailsService) {
        this.jwtService = jwtService;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/api/v2/users/register")
                || path.equals("/api/v2/users/login")
                || path.equals("/api/v2/users/register-moderator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;




        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            username = jwtService.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        System.out.println("Header: " + header);
        System.out.println("Extracted username: " + username);
        filterChain.doFilter(request, response);
    }

}
