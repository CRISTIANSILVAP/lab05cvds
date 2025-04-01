package edu.eci.cvds.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Permite todas las rutas
                .allowedOrigins("https://red-rock-00cb7d11e.6.azurestaticapps.net") // URL de tu frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Métodos permitidos o end points permitidos
                .allowedHeaders("*"); // Permitir todos los encabezados
    }
}