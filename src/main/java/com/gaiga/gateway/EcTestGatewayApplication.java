package com.gaiga.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class EcTestGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcTestGatewayApplication.class, args);
	}

}
