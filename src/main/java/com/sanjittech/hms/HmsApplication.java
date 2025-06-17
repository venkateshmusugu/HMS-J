package com.sanjittech.hms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EnableJpaRepositories(basePackages = "com.sanjittech.hms.repository")
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class HmsApplication {

	public static void main(String[] args) {

		SpringApplication.run(HmsApplication.class, args);
	}

}
