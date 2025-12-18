package ar.org.adriel_medero.java.pickyourmon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // spring detecta esta clase como una clase de configuración
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // aplica a TODAS las URLs de la API
                .allowedOrigins("*") // permite peticiones desde CUALQUIER lugar (Frontend, Postman, Celular)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // las acciones permitidas
                .allowedHeaders("*"); // permite cualquier cabecera (tokens, etc)
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {   
        // Esto redirige localhost:8080 -> localhost:8080/home.html
        registry.addViewController("/").setViewName("forward:/home.html");
    }
}