package ru.ssau.s_rest_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SRestAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SRestAppApplication.class, args);
	}

}
