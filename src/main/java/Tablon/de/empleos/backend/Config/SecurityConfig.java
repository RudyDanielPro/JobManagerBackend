package Tablon.de.empleos.backend.Config;

import Tablon.de.empleos.backend.Repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 0. Rutas de Swagger PROTEGIDAS (Requieren autenticación Básica)
                .requestMatchers(
                    "/v3/api-docs/**", 
                    "/swagger-ui/**", 
                    "/swagger-ui.html",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).hasRole("ADMIN")

                // 1. Rutas PÚBLICAS
                .requestMatchers("/api/auth/**").permitAll() 
                .requestMatchers("/api/ofertas/**").permitAll() 
                .requestMatchers("/api/postulaciones/enviar").permitAll() 
                
                // 2. Rutas para RECLUTADORES y ADMIN
                .requestMatchers("/api/empresas/**").hasAnyRole("RECRUITER", "ADMIN")
                
                // 3. Resto de rutas de la API
                .requestMatchers("/api/**").hasRole("ADMIN")
                
                .anyRequest().authenticated()
            )
            // Activa la ventana emergente del navegador
            .httpBasic(httpBasic -> {}); 

        return http.build();
    }

    // ESTE BLOQUE ES EL QUE SOLUCIONA EL 401
    // Conecta la seguridad con los usuarios de tu BD
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsuario(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsuario())
                        .password(user.getPassword()) // Ya debe estar encriptada en la BD
                        .authorities(user.getRol())   // Asegúrate de que el rol sea "ROLE_ADMIN"
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}