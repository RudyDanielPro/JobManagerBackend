package Tablon.de.empleos.backend.Config;

import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Entity.UserFoto;
import Tablon.de.empleos.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_USER}")
    private String adminUser;

    @Value("${ADMIN_PASS}")
    private String adminPass;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;


    @Value("${ADMIN_FOTO_URL") 
    private String adminFotoUrl;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsuario(adminUser)) {
            User admin = new User();
            admin.setNombre("Admin"); // Añadí estos campos para que no den error de validación
            admin.setApellido("Sistema");
            admin.setUsuario(adminUser);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPass)); 
            admin.setRol("ROLE_ADMIN");

            // --- Lógica para la Imagen ---
            UserFoto fotoAdmin = new UserFoto();
            fotoAdmin.setRuta(adminFotoUrl);
            fotoAdmin.setNombreArchivo("avatar_admin.png");
            
            admin.setFoto(fotoAdmin); // Vinculamos la entidad UserFoto
            // -----------------------------

            userRepository.save(admin);
            System.out.println("SISTEMA: Usuario ADMIN creado con foto desde variables.");
        }
    }

}