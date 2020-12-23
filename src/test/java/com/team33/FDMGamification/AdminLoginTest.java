package com.team33.FDMGamification;

import com.team33.FDMGamification.Model.Admin;
import com.team33.FDMGamification.Model.AdminDetails;
import com.team33.FDMGamification.Service.AdminDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration
public class AdminLoginTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    private AdminDetails admin;

    @Autowired
    private AdminDetailsService ads;

    @Autowired
    private Environment env;

    @BeforeEach
    public void setup(){
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        admin = new AdminDetails(new Admin(env.getProperty("admin.username")
                , env.getProperty("admin.firstname")
                , env.getProperty("admin.lastname")
                , env.getProperty("admin.email")
                , env.getProperty("admin.password")
                , env.getProperty("admin.phoneNo")));
    }

    @Test
    public void testAdminAccessToAdminSite_ShouldBeOK(){
        assertDoesNotThrow(
                () -> mvc.perform(formLogin()
                        .user(admin.getUsername())
                        .password(admin.getPassword()))
                .andExpect(authenticated().withRoles("ADMIN"))) ;
    }

    @Test
    public void testPublicAccessToAdminSite_ShouldFail(){
        assertDoesNotThrow(
                () -> mvc.perform(get("/admin"))
                        .andExpect(unauthenticated())) ;
    }

    @Test
    public void testAdminAccessToPublicSite_ShouldBeOK(){
        assertDoesNotThrow(
                () -> mvc.perform(get("/home")
                        .with(user(admin)))
                        .andExpect(status().isOk())
                        .andExpect(authenticated())) ;
    }

    @Test
    public void testPublicAccessToPublicSite_ShouldBeOK(){
        assertDoesNotThrow(
                () -> mvc.perform(get("/home"))
                        .andExpect(status().isOk())
                        .andExpect(unauthenticated())) ;
    }
}
