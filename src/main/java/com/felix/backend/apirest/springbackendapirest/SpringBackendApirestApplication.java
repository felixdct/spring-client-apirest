package com.felix.backend.apirest.springbackendapirest;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBackendApirestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBackendApirestApplication.class, args);
	}
	
	@PostConstruct
	void started() {
	    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
}
