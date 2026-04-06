package Tablon.de.empleos.backend.Security;

import Tablon.de.empleos.backend.Entity.Candidato;
import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Entity.UserFoto;
import Tablon.de.empleos.backend.Repository.CandidatoRepository;
import Tablon.de.empleos.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CandidatoRepository candidatoRepository;
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
                           CandidatoRepository candidatoRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.candidatoRepository = candidatoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            boolean adminExists = userRepository.existsByUsuario(adminUser) || 
                                  userRepository.existsByEmail(adminEmail);
            
            if (!adminExists) {
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

                User savedAdmin = userRepository.save(admin);

                Candidato adminCandidato = new Candidato(adminNombre, adminApellido);
                adminCandidato.setId(savedAdmin.getId());
                adminCandidato.setUsuario(savedAdmin);

                savedAdmin.setCandidato(adminCandidato);

                candidatoRepository.save(adminCandidato);

                System.out.println("✅ SISTEMA: Usuario ADMIN creado con éxito!");
                System.out.println("   Nombre: " + adminNombre + " " + adminApellido);
                System.out.println("   Usuario: " + adminUser);
                System.out.println("   Email: " + adminEmail);
                System.out.println("   Rol: ADMIN");
            } else {
                System.out.println("ℹ️ SISTEMA: El usuario ADMIN ya existe.");
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR al crear usuario ADMIN: " + e.getMessage());
            e.printStackTrace();
        }
    }
}