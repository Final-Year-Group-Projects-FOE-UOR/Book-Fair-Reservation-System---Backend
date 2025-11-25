package com.bookfair.bookfairreservationsystembackend.models.user;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {
    private final User user;
    public UserPrincipal(User user) {
        this.user=user;
    }
    public User getUser() {
        return this.user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = user.getRole() != null ? user.getRole().name() : "";
        System.out.println("User role: " + roleName); // Debug
        return Collections.singleton(new SimpleGrantedAuthority(roleName));
    }
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        if (user.getRole() == null) {
//            return Collections.emptyList();
//        }
//        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
//    }
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    @Override
    public String getUsername() {
        return user.getEmail();
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
    public boolean isCredentialsNonExpired() {return true;}

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}