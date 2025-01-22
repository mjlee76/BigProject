package com.bigProject.tellMe.config;

import com.bigProject.tellMe.entity.User;
import com.bigProject.tellMe.enumClass.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("serial")
public class TellMeUserDetails implements UserDetails {
    private User user;

    public TellMeUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role roles = user.getRole();

        List<SimpleGrantedAuthority> authories = new ArrayList<>();


        authories.add(new SimpleGrantedAuthority(roles.toString()));


        return authories;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

//    public String getFullname() {
//        return user.getName();
//    }
}
