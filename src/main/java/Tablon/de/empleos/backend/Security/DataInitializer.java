package Tablon.de.empleos.backend.Security;

import Tablon.de.empleos.backend.Entity.Admin;
import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Entity.UserFoto;
import Tablon.de.empleos.backend.Repository.AdminRepository;
import Tablon.de.empleos.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
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

    public DataInitializer(UserRepository userRepository,
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            boolean adminExists = userRepository.existsByUsuario(adminUser) ||
                    userRepository.existsByEmail(adminEmail);

            if (!adminExists) {
                System.out.println("Creando usuario ADMIN...");

                // 1. Crear el User
                User admin = new User();
                admin.setUsuario(adminUser);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPass));
                admin.setRol("ADMIN");

                if (adminFotoUrl != null && !adminFotoUrl.isEmpty()) {
                    UserFoto fotoAdmin = new UserFoto();
                    fotoAdmin.setRuta(adminFotoUrl);
                    fotoAdmin.setNombreArchivo("avatar_admin.png");
                    admin.setFoto(fotoAdmin);
                }

                User savedAdmin = userRepository.saveAndFlush(admin);
                System.out.println("ID de usuario generado: " + savedAdmin.getId());

                Admin adminEntity = new Admin(adminNombre, adminApellido);
                adminEntity.setId(savedAdmin.getId());
                adminEntity.setUsuario(savedAdmin);

                savedAdmin.setAdmin(adminEntity);

                adminRepository.save(adminEntity);
                userRepository.save(savedAdmin);

                System.out.println("SISTEMA: Usuario ADMIN creado con exito!");
                System.out.println("   Nombre: " + adminNombre + " " + adminApellido);
                System.out.println("   Usuario: " + adminUser);
                System.out.println("   Email: " + adminEmail);
                System.out.println("   Rol: ADMIN");
            } else {
                System.out.println("SISTEMA: El usuario ADMIN ya existe.");
            }
        } catch (Exception e) {
            System.err.println("ERROR al crear usuario ADMIN: " + e.getMessage());
            e.printStackTrace();
        }
    }
}