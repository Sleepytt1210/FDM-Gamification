package com.team33.FDMGamification.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AdminDetails extends Admin implements UserDetails {

    public AdminDetails(String username, String firstname, String lastname, String email, String password, String phoneNo, Integer id) {
        super(username, firstname, lastname, email, password, phoneNo);
        setId(id);
    }

    public AdminDetails(Admin admin) {
        super(admin.getUsername(), admin.getFirstname(), admin.getLastname(),admin.getEmail(), admin.getPassword(), admin.getPhoneNo());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return grantedAuthorities;
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

    @Override
    public String toString() {
        return super.toString();
    }
}
