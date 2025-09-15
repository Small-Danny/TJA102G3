package com.tibafit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.tibafit")
public class Tja102G3Application {

	public static void main(String[] args) {
		SpringApplication.run(Tja102G3Application.class, args);
	}

}
