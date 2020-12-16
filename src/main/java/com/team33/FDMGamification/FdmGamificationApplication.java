package com.team33.FDMGamification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class FdmGamificationApplication {

	private final Logger log = LoggerFactory.getLogger(FdmGamificationApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FdmGamificationApplication.class, args);
	}

}
