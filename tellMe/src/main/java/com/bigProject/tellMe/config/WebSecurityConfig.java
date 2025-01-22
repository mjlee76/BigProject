package com.bigProject.tellMe.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final CustomAuthSuccessHandler customAuthSuccessHandler;
    private final CustomAuthFailureHandler customAuthFailureHandler;

    //사용자인증을 처리하기 위한 컴포넌트
    //AuthenticationManager : 인증을 처리하는 핵심 객체, 사용자 아이디와 비밀번호를 확인하여 인증을 처리하는 역할
    //Bean으로 등록함으로써 IoC에서 자동으로 관리하도록 함.

    //HttpSecurity : Spring Security에서 보안 설정을 담당하는 객체
    //getSharedObject() : Spring Security 내부에서 사용되는 공유 객체를 가져오는 메서드
    //AuthenticationManagerBuilder : AuthenticationManager를 빌드하는 데 사용되는 빌더 객체
    //HttpSecurity에서 빌더 객체를 가져옴.

    //authenticationProvider : AuthenticationManager에 사용할 인증 제공자를 설정, DaoAuthenticationProvider를 사용
    //빌더를 통해 설정한 후 AuthenticationManager 객체를 생성하고 반환
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    //UserDetailsService**를 통해 사용자의 정보를 조회하고,
    //PasswordEncoder를 사용하여 암호화된 비밀번호를 확인하는 방식으로 사용자 인증을 처리
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    //사용자 정보 부분은 우리가 따로 파일을 만들어서 관리
    @Bean
    public UserDetailsService userDetailsService() {
        return new TellMeUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").hasAnyRole("USER")
                        .requestMatchers("/complaint/**").hasAnyRole("USER")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("userId")
                        .failureHandler(customAuthFailureHandler)
                        .successHandler(customAuthSuccessHandler)
                        .permitAll()

                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/fontawesome/**", "/webjars/**", "/webfonts/**");
    }
}
