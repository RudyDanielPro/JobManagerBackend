package Tablon.de.empleos.backend.Security;

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

    @Value("${ADMIN_FOTO_URL}")
    private String adminFotoUrl;

    @Value("${ADMIN_NAME}")
    private String adminNombre;

    @Value("${ADMIN_APELLIDO}")
    private String adminApellido;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            boolean adminExists = userRepository.existsByUsuario(adminUser) || 
                                  userRepository.existsByEmail(adminEmail);
            
            if (!adminExists) {
                User admin = new User();
                admin.getCandidato().setNombre(adminNombre);
                admin.getCandidato().setApellido(adminApellido);
                admin.setUsuario(adminUser);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPass));
                admin.setRol("ROLE_ADMIN");

                UserFoto fotoAdmin = new UserFoto();
                fotoAdmin.setRuta(adminFotoUrl);
                fotoAdmin.setNombreArchivo("avatar_admin.png");
                admin.setFoto(fotoAdmin);

                userRepository.save(admin);
                System.out.println("✅ SISTEMA: Usuario ADMIN creado con éxito!");
            } else {
                System.out.println("ℹ️ SISTEMA: El usuario ADMIN ya existe. No se requiere creación.");
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR al crear usuario ADMIN: " + e.getMessage());
            e.printStackTrace();
        }
    }
}