package com.cross2u.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class StoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);
	}

	@LoadBalanced
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder)
	{
		return restTemplateBuilder.setConnectTimeout(100000000).build();
	}

}

