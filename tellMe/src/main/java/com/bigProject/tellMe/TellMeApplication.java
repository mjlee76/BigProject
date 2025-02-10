package com.bigProject.tellMe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing // CreatedDate 어노테이션을 사용하려면 Auditing 기능이 활성화 돼있어야한다. Auditing 을 활성화해주는 역할.
@EnableFeignClients
public class TellMeApplication {

	public static void main(String[] args) {

		SpringApplication.run(TellMeApplication.class, args);
	}

}
