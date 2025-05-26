package io.github.chefiit.config;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class OpenApiConfig {

	@Autowired
    private DataSource dataSource;
    

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().title("Chefit API").version("1.0").description("Descrição da sua API")
				.contact(new Contact().name("Guilherme Melvin").email("seu.email@exemplo.com")));
	}

	@PostConstruct
    public void showDatabaseInfo() {
        try {
            System.out.println("Database URL: " + dataSource.getConnection().getMetaData().getURL());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
