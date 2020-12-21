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

    /**
     * Insert and persist data into Admin Table to create a new admin user.
     * @param username Username of admin.
     * @param firstname Firstname of admin.
     * @param lastname Lastname of admin.
     * @param email Email of admin.
     * @param password Password of admin.
     * @param phoneNo Phone number of admin.
     * @return Admin: Admin object persisted in database.
     */
    public Admin createUser(String username, String firstname, String lastname, String email, String password, String phoneNo) {
        Admin admin = new Admin(username, firstname, lastname, email, passwordEncoder.encode(password), phoneNo);
        return adminRepo.saveAndFlush(admin);
    }

    /**
     * Find and load admin user by its username for authentication.
     * @param username Username of admin.
     * @return AdminDetails: A wrapper class for UserDetails which store extra details to current active user.
     * @throws UsernameNotFoundException If admin user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepo.findByUsername(username);
        if(admin == null || !admin.getUsername().equals(username)){
            throw new UsernameNotFoundException("Admin with username " + username + " is not found!");
        }
        return new AdminDetails(admin.getUsername(), admin.getFirstname(), admin.getLastname(), admin.getEmail(), admin.getPassword(), admin.getPhoneNo(), admin.getId());
    }
}
