package com.bigProject.tellMe.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
        for (GrantedAuthority role : roles) {

            if (role.getAuthority().equals("ROLE_USER")) {
                redirectStrategy.sendRedirect(request, response, "/");
            } else if (role.getAuthority().equals("ROLE_COUNSELOR")) {
                redirectStrategy.sendRedirect(request, response, "/");
            } else if (role.getAuthority().equals("ROLE_MANAGER")) {
                redirectStrategy.sendRedirect(request, response, "/");
            } else if (role.getAuthority().equals("ROLE_ADMIN")) {
                redirectStrategy.sendRedirect(request, response, "/");
            }
        }
    }
}
