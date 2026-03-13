package Tablon.de.empleos.backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 1. Rutas PÚBLICAS (Deben ir primero)
                .requestMatchers("/api/auth/**").permitAll() 
                .requestMatchers("/api/ofertas/**").permitAll() 
                .requestMatchers("/api/postulaciones/enviar").permitAll() 
                
                // 2. Rutas para RECLUTADORES
                .requestMatchers("/api/empresas/**").hasAnyRole("RECRUITER", "ADMIN")
                
               
                .requestMatchers("/api/**").hasRole("ADMIN")
                
                // 4. Cualquier otra ruta requiere estar logueado
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> {}); 

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración de CORS para que React pueda conectarse desde Render/Localhost
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") // En producción cambia el * por la URL de tu frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}