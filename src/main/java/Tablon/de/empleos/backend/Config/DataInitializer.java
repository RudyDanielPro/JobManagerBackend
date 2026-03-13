package Tablon.de.empleos.backend.Config;

import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Leemos los valores desde el entorno o usamos valores por defecto
    @Value("${ADMIN_USER}")
    private String adminUser;

    @Value("${ADMIN_PASS}")
    private String adminPass;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsuario(adminUser)) {
            User admin = new User();
            admin.setUsuario(adminUser);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPass)); 
            admin.setRol("ROLE_ADMIN");
            userRepository.save(admin);
            System.out.println("SISTEMA: Usuario ADMIN configurado desde variables de entorno.");
        }
    }
}