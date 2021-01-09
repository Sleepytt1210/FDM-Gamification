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

    /**
     * Configure security level base on URL pattern. All existing url patterns except admin urls are accessible by public user.
     * @param http HttpSecurity Bean to configure security access.
     * @throws Exception Authorize Requests error.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/**").permitAll()
            .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
            .and()
                .logout()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/home?logout");
    }

    /**
     * Configure Spring Boot Security to use custom admin details service and create a default admin user with details from application properties.
     * @param builder An AuthenticationManagerBuilder bean.
     * @throws Exception If an error occurs when adding the UserDetailsService based authentication
     */
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

    /**
     * Configure a password encoder and hashing algorithm to be used by authentication service.
     * @return A password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
