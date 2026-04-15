package Tablon.de.empleos.backend.Security;

import Tablon.de.empleos.backend.Entity.Admin;
import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Entity.UserFoto;
import Tablon.de.empleos.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)  // Ejecutar después de que la aplicación esté lista
public class DataInitializer implements ApplicationRunner {  // ← Cambiar a ApplicationRunner

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
    @Transactional
    public void run(ApplicationArguments args) {  // ← Cambiar el parámetro
        try {
            // Pequeño delay para asegurar que todo está listo
            Thread.sleep(100);
            
            boolean adminExists = userRepository.existsByUsuario(adminUser) || 
                                  userRepository.existsByEmail(adminEmail);
            
            if (!adminExists) {
                System.out.println("Creando usuario ADMIN...");

                Admin adminEntity = new Admin(adminNombre, adminApellido);

                User admin = new User();
                admin.setUsuario(adminUser);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPass));
                admin.setRol("ADMIN");
                admin.setAdmin(adminEntity);  
                adminEntity.setUsuario(admin); 

                if (adminFotoUrl != null && !adminFotoUrl.isEmpty()) {
                    UserFoto fotoAdmin = new UserFoto();
                    fotoAdmin.setRuta(adminFotoUrl);
                    fotoAdmin.setNombreArchivo("avatar_admin.png");
                    admin.setFoto(fotoAdmin);
                }

                User savedAdmin = userRepository.save(admin);
                

                System.out.println("SISTEMA: Usuario ADMIN creado con exito!");
            } else {
                System.out.println("SISTEMA: El usuario ADMIN ya existe.");
            }
        } catch (Exception e) {
            System.err.println("ERROR al crear usuario ADMIN: " + e.getMessage());
            e.printStackTrace();
        }
    }
}