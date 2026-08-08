package tancredidangelo.capstone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import static tancredidangelo.capstone.helpers.CountryCodeConverter.toIsoCode;

@SpringBootApplication
@EnableAsync
public class CapstoneApplication {

	static void main(String[] args) {
		SpringApplication.run(CapstoneApplication.class, args);
	}

}
