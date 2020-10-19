package com.siri.spring.integration.selectorgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.integration.config.EnableIntegration;

@EnableIntegration
@EnableCaching
@SpringBootApplication
public class SelectorgatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SelectorgatewayApplication.class, args);
	}

}
