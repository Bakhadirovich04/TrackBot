package uz.husan.signup.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import uz.husan.signup.entity.User;

@OpenAPIDefinition(

        info =@Info(
             title = "Signup Service API",
                version = "1.0",
             contact = @Contact(
                        name = "Husan Choriyev",
                        email = "choriyevhusan74@gmail.com",url = "https://github.com/bakhadirovich04"
             )
        ),
        externalDocs = @ExternalDocumentation(
                description = "Spring 6 wiki Documentation", url = "https://springshop.wiki.github.org/docs"
        ),
        servers = {
                @Server(
                        url = "http://localhost:8081",
                        description = "Local server"
                ),
                @Server(
                        url = "https://10.10.1.111:8081",
                        description = "Production server"
                )

        },
        security ={
                @SecurityRequirement(
                        name = "BearerAuth"
                )
        }
)
@SecurityScheme(
    name = "BearerAuth",
    description = "JWT Auth description",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
