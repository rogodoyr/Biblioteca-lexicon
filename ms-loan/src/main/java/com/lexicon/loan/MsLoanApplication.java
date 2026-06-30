package com.lexicon.loan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsLoanApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsLoanApplication.class, args);
	}

}
