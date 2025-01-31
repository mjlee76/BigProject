package com.bigProject.tellMe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TellMeApplication {

	public static void main(String[] args) {

		SpringApplication.run(TellMeApplication.class, args);
	}

}
