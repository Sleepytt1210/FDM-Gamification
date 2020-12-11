package com.team33.FDMGamification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {

    @Value("${database.driver}")
    private String driverClass;

    @Value("${database.host}")
    private String host;

    @Value("${database.name}")
    private String database;

    @Value("${database.port}")
    private int port;

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    @Autowired
    private SSHTunnel tunnel;

    @Bean
    public DataSource dataSource(){
        String dbURL = "jdbc:mysql://" + host + ":" + port + "/" + database;
        DataSourceBuilder builder = DataSourceBuilder.create();
        builder.driverClassName(driverClass);
        builder.url(dbURL);
        builder.username(username);
        builder.password(password);
        return builder.build();
    }

}
