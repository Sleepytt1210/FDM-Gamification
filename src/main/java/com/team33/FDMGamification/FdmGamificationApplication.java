package com.team33.FDMGamification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;

import javax.sql.DataSource;
import java.util.List;

@SpringBootApplication
public class FdmGamificationApplication {

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(FdmGamificationApplication.class, args);
	}

	@Bean
	public CommandLineRunner run() {
		return args -> {

		};
	}
}
