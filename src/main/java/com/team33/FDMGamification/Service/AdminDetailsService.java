package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.AdminRepository;
import com.team33.FDMGamification.Model.Admin;
import com.team33.FDMGamification.Model.AdminDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Admin createUser(String username, String firstname, String lastname, String email, String password, String phoneNo) {
        Admin admin = new Admin(username, firstname, lastname, email, passwordEncoder.encode(password), phoneNo);
        return adminRepo.saveAndFlush(admin);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepo.findByUsername(username);
        if(admin == null || !admin.getUsername().equals(username)){
            throw new UsernameNotFoundException("Admin with username " + username + " is not found!");
        }
        return new AdminDetails(admin.getUsername(), admin.getFirstname(), admin.getLastname(), admin.getEmail(), admin.getPassword(), admin.getPhoneNo(), admin.getId());
    }
}
