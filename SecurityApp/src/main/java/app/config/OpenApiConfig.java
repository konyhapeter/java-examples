package app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI securityAppOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("SecurityApp CRUD API")
                        .description("OAuth2 JWT protected API compatible with Keycloak and Auth0.")
                        .version("v1"))
                .components(new Components().addSecuritySchemes("oauth2",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows().password(new OAuthFlow()
                                        .tokenUrl("http://localhost:8080/realms/security-demo/protocol/openid-connect/token")
                                        .scopes(new Scopes()
                                                .addString("openid", "OpenID Connect")
                                                .addString("profile", "User profile"))))));
    }
}
