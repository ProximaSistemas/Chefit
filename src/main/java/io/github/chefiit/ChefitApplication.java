package io.github.chefiit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "io.github.chefiit.repository")
public class ChefitApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChefitApplication.class, args);
    }
}