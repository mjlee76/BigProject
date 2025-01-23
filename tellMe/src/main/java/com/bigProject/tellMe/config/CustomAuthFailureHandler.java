package com.bigProject.tellMe.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String errorMessage = null;

        if (exception  instanceof BadCredentialsException) {
            String username = request.getParameter("userId");
            try {
                // 사용자 이름으로 아이디가 존재하는지 먼저 확인
                userDetailsService.loadUserByUsername(username);
                errorMessage = "비밀번호를 확인해주세요."; // 아이디는 맞지만 비밀번호가 틀린 경우
            } catch (UsernameNotFoundException e) {
                errorMessage = "아이디와 비밀번호를 확인해주세요."; // 아이디가 없을 경우
            }
        } else if (exception  instanceof InternalAuthenticationServiceException) {
            errorMessage = "내부 시스템 문제로 로그인할 수 없습니다. 관리자에게 문의하세요.";
        } else if (exception  instanceof UsernameNotFoundException) {
            errorMessage = "존재하지 않는 계정입니다. 회원가입을 해주세요.";
        } else {
            errorMessage = "알 수없는 오류입니다.";
        }

        request.getSession().setAttribute("loginErrorMessage", errorMessage);
        response.sendRedirect("/tellMe/login?error=true");
    }
}
