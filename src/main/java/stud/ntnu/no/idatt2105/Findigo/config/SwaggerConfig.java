package stud.ntnu.no.idatt2105.Findigo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Findigo API")
            .version("1.0")
            .description("API for Findigo, the final project in IDATT2105 Full-stack applikasjonsutvikling")
            .contact(new Contact()
                .name("Scott du Plessis, Aryan Malekian, Mikael Stray Frøyshov, Jonathan Hubertz")
                .email("scottld@ntnu.no")));
  }
}