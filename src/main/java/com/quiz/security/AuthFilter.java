package com.quiz.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final QuizUserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String token = getToken(request);
            if(!StringUtils.isEmpty(token) && tokenProvider.validateToken(token)){
                String email = tokenProvider.getUserEmailFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,userDetails,userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.warn("User can`t auth");
        }
        chain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (!StringUtils.isEmpty(token) && token.startsWith("Bearer ")) {
            return token.replace("Bearer ", "");
        }
        return null;
    }
}
