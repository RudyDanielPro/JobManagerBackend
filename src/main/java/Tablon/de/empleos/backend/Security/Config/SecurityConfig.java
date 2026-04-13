package Tablon.de.empleos.backend.Security.Config;

import Tablon.de.empleos.backend.Security.jwt.JwtAuthenticationFilter;
import Tablon.de.empleos.backend.Repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserRepository userRepository, 
                          @Lazy JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ============ ENDPOINTS PÚBLICOS (SIN AUTENTICACIÓN) ============
                
                // Autenticación y registro
                .requestMatchers("/api/auth/**").permitAll()
                
                // Ofertas públicas (listar, buscar, ver detalle)
                .requestMatchers("/api/ofertas/public/**").permitAll()
                
                // Enviar postulación (requiere token internamente pero endpoint expuesto)
                .requestMatchers("/api/postulaciones/enviar").permitAll()
                
                // Aprobación de postulaciones por email (con token único)
                .requestMatchers("/api/aprobacion/**").permitAll()
                
                // GET de empresas y candidatos son públicos (para obtener logos, contar, etc.)
                .requestMatchers(HttpMethod.GET, "/api/empresas/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/candidatos/**").permitAll()
                
                // ============ ENDPOINTS DE ADMINISTRACIÓN ============
                
                // Panel de administración (solo ADMIN)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Documentación Swagger/OpenAPI (solo ADMIN)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", 
                                 "/swagger-resources/**", "/webjars/**").hasRole("ADMIN")
                
                // ============ OPERACIONES DE ESCRITURA PARA EMPRESAS ============
                
                .requestMatchers(HttpMethod.POST, "/api/empresas/**").hasAnyRole("RECRUITER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/empresas/**").hasAnyRole("RECRUITER", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/empresas/**").hasAnyRole("RECRUITER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/empresas/**").hasAnyRole("RECRUITER", "ADMIN")
                
                // ============ OPERACIONES DE ESCRITURA PARA CANDIDATOS ============
                
                .requestMatchers(HttpMethod.POST, "/api/candidatos/**").hasAnyRole("CANDIDATO", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/candidatos/**").hasAnyRole("CANDIDATO", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/candidatos/**").hasAnyRole("CANDIDATO", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/candidatos/**").hasAnyRole("CANDIDATO", "ADMIN")
                
                // ============ ENDPOINTS QUE REQUIEREN AUTENTICACIÓN ============
                
                // Ofertas (excepto las públicas ya definidas)
                .requestMatchers("/api/ofertas/**").authenticated()
                
                // Postulaciones (excepto enviar que ya es público)
                .requestMatchers("/api/postulaciones/**").authenticated()
                
                // ============ CUALQUIER OTRA PETICIÓN ============
                
                .anyRequest().hasRole("ADMIN")
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",      // React (Create React App)
            "http://localhost:5173",      // Vite
            "http://localhost:8080",      // Desarrollo local
            "http://localhost:4200",      // Angular
            "https://jobmanagerbackend.onrender.com"  // Producción
        ));
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Tablon.de.empleos.backend.Entity.User user = userRepository.findByEmail(username)
                .orElseGet(() -> userRepository.findByUsuario(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username)));
            
            String roleName = user.getRol();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName.toUpperCase();
            }
            
            return User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities(roleName)
                    .build();
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}