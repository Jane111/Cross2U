package com.cross2u.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@SpringBootApplication
@EnableEurekaClient
public class WareApplication {

	public static void main(String[] args) {
		SpringApplication.run(WareApplication.class, args);
	}


	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
}

