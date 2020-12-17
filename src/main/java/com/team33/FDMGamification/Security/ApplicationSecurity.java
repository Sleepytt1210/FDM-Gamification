package com.team33.FDMGamification.Security;

import com.team33.FDMGamification.Service.AdminDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private AdminDetailsService adminDetailsService;

    @Autowired
    private Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/**").permitAll()
                .and().formLogin()
                .and()
                .logout()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/home?logout");
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(adminDetailsService).passwordEncoder(passwordEncoder()).getUserDetailsService()
                .createUser(env.getProperty("admin.username")
                        , env.getProperty("admin.firstname")
                        , env.getProperty("admin.lastname")
                        , env.getProperty("admin.email")
                        , env.getProperty("admin.password")
                        , env.getProperty("admin.phoneNo"));
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
