package edu.polytech.caissatn.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "CaissaTN - API",
                version = "1.0.0",
                description = "Documentation OpenAPI pour le backend CaissaTN avec synchronisation offline/online.",
                contact = @Contact(
                        name = "Donia Chaouch",
                        email = "dchaouch01@gmail.com"

                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                ),
                termsOfService = "https://your-app.com/terms"
        ),
        servers = {
                @Server(
                        description = "Environnement local",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Environnement de production",
                        url = "https://your-production-url.com"
                )
        }
)
public class OpenAPIConfig {
}
