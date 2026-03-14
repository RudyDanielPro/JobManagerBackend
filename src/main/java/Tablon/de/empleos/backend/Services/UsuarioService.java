package Tablon.de.empleos.backend.Services;

import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Entity.UserFoto;
import Tablon.de.empleos.backend.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    // Inyección por constructor (la forma más recomendada en Spring Boot)
    public UsuarioService(UserRepository userRepository, 
                          CloudinaryService cloudinaryService, 
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param usuario Objeto con los datos del usuario (nombre, email, usuario, password, etc.)
     * @param imagen Archivo de imagen opcional para el perfil.
     * @return El usuario guardado con su contraseña encriptada y foto vinculada.
     * @throws IOException Si ocurre un error al subir la imagen a Cloudinary.
     */
    @Transactional
    public User registrarUsuario(User usuario, MultipartFile imagen) throws IOException {
        
        // 1. Encriptar la contraseña antes de guardarla (Seguridad obligatoria)
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // 2. Procesar la imagen si el usuario subió una
        if (imagen != null && !imagen.isEmpty()) {
            // Subimos a Cloudinary usando el servicio que creamos
            Map result = cloudinaryService.upload(imagen);
            String urlCloudinary = result.get("url").toString();

            // 3. Crear la entidad UserFoto según tu estructura original
            UserFoto foto = new UserFoto();
            foto.setRuta(urlCloudinary);
            foto.setNombreArchivo(imagen.getOriginalFilename());

            // 4. Vincular la foto al usuario (Relación @OneToOne)
            usuario.setFoto(foto);
        }

        // 5. Guardar el usuario en la base de datos (PostgreSQL)
        return userRepository.save(usuario);
    }

    
    public User autenticarUsuario(String identificador, String password) {
        Optional<User> userOpt = userRepository.findByEmail(identificador);
        if (!userOpt.isPresent()) {
            userOpt = userRepository.findByUsuario(identificador);
        }
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}