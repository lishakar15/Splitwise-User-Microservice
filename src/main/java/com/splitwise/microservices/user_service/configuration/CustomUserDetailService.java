package com.splitwise.microservices.user_service.configuration;

import com.splitwise.microservices.user_service.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetailService implements UserDetails {
    private final User user;

    public CustomUserDetailService(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Populate the authorities/roles
        return Collections.emptyList(); // or use actual roles
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    public Long getUserId(){
        return user.getUserId();
    }

    @Override
    public String getUsername() {
        return user.getEmailId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
