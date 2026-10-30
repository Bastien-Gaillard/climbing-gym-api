package fr.climbinggym.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI climbingGymOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Climbing Gym API")
                .description("Adhérents, abonnements, créneaux, réservations et matériel d'une salle d'escalade")
                .version("v1"));
    }
}
