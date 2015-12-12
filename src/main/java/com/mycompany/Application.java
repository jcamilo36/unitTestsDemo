package com.mycompany;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot application configuration and main class.
 * Created by jcortes on 12/9/15.
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
public class Application extends SpringBootServletInitializer {

    /**
     * Main Method to start Spring Boot application.
     *
     * @param args String Main Args
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Sample bootApplication method.
     */
    public void bootApplication() {
    }
}
