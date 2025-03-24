package stud.ntnu.no.idatt2105.Findigo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class used to configure Swagger.
 */
@Configuration
public class SwaggerConfig {
  /**
   * Configures and provides a custom OpenAPI bean for the Findigo API.
   *
   * <p>This method initializes an OpenAPI object with metadata including the title, version,
   * description, and contact information for the Findigo API.</p>
   *
   * @return an {@link OpenAPI} instance with predefined API metadata.
   */
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