package edu.eci.cvds.project;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class Lab04Application {

	public static void main(String[] args) {
		SpringApplication.run(Lab04Application.class, args);
	}
}