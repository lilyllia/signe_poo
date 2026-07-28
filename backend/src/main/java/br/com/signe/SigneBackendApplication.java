package br.com.signe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@EntityScan(basePackages = "br.com.signe")
@SpringBootApplication
public class SigneBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(SigneBackendApplication.class, args);
	}
}
