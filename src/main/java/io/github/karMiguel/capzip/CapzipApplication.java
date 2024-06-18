package io.github.karMiguel.capzip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class CapzipApplication {

	public static void main(String[] args) {
		SpringApplication.run(CapzipApplication.class, args);
	}

}
