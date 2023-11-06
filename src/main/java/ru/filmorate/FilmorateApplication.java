package ru.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Filmorate application
 */

@SpringBootApplication
public class FilmorateApplication {

	/**
	 * The entry point of application.
	 * @param args the input arguments
	 */

	public static void main(String[] args) {
		SpringApplication application =
				new SpringApplication(FilmorateApplication.class);
		application.setAdditionalProfiles("development");
		application.run(args);
	}
}