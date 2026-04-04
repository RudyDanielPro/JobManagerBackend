package Tablon.de.empleos.backend.Services;

import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Entity.UserFoto;
import Tablon.de.empleos.backend.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            CloudinaryService cloudinaryService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registrarUsuario(User usuario, MultipartFile imagen) throws IOException {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        if (imagen != null && !imagen.isEmpty()) {
            Map result = cloudinaryService.upload(imagen);
            String urlCloudinary = result.get("url").toString();

            UserFoto foto = new UserFoto();
            foto.setRuta(urlCloudinary);
            foto.setNombreArchivo(imagen.getOriginalFilename());
            usuario.setFoto(foto);
        }

        return userRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Optional<User> buscarPorId(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> buscarPorUsuario(String usuario) {
        return userRepository.findByUsuario(usuario);
    }

    @Transactional(readOnly = true)
    public Optional<User> buscarPorIdentificador(String identificador) {
        Optional<User> userOpt = userRepository.findByEmail(identificador);
        if (userOpt.isPresent()) {
            return userOpt;
        }
        return userRepository.findByUsuario(identificador);
    }

    @Transactional(readOnly = true)
    public List<User> listarPorRol(String rol) {
        return userRepository.findByRol(rol);
    }

    @Transactional(readOnly = true)
    public Page<User> listarPorRolPaginado(String rol, Pageable pageable) {
        return userRepository.findByRol(rol, pageable); // ✅ Con paginación
    }

    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existePorUsuario(String usuario) {
        return userRepository.existsByUsuario(usuario);
    }

    // Autenticar usuario (login)

    @Transactional(readOnly = true)
    public Optional<User> autenticarUsuario(String identificador, String passwordRaw) {
        Optional<User> userOpt = buscarPorIdentificador(identificador);

        if (userOpt.isPresent() && passwordEncoder.matches(passwordRaw, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    // Actualizar usuario

    @Transactional
    public User actualizarUsuario(Long id, User datosNuevos, User usuarioAutenticado) {
        User usuarioExistente = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        boolean esMismoUsuario = usuarioExistente.getId().equals(usuarioAutenticado.getId());
        boolean esAdmin = "ADMIN".equals(usuarioAutenticado.getRol());

        if (!esMismoUsuario && !esAdmin) {
            throw new RuntimeException("No tienes permisos para actualizar este usuario");
        }
        if (datosNuevos.getEmail() != null) {
            usuarioExistente.setEmail(datosNuevos.getEmail());
        }
        if (datosNuevos.getUsuario() != null) {
            usuarioExistente.setUsuario(datosNuevos.getUsuario());
        }

        return userRepository.save(usuarioExistente);
    }

    @Transactional
    public void cambiarContraseña(Long id, String nuevaContraseña, User usuarioAutenticado) {
        User usuarioExistente = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        boolean esMismoUsuario = usuarioExistente.getId().equals(usuarioAutenticado.getId());
        boolean esAdmin = "ADMIN".equals(usuarioAutenticado.getRol());

        if (!esMismoUsuario && !esAdmin) {
            throw new RuntimeException("No tienes permisos para cambiar esta contraseña");
        }

        usuarioExistente.setPassword(passwordEncoder.encode(nuevaContraseña));
        userRepository.save(usuarioExistente);
    }

    @Transactional
    public User actualizarFotoPerfil(Long id, MultipartFile imagen, User usuarioAutenticado) throws IOException {
        User usuario = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (!usuario.getId().equals(usuarioAutenticado.getId())) {
            throw new RuntimeException("No tienes permisos para cambiar la foto de este usuario");
        }

        if (imagen != null && !imagen.isEmpty()) {
            Map result = cloudinaryService.upload(imagen);
            String urlCloudinary = result.get("url").toString();

            UserFoto foto = usuario.getFoto();
            if (foto == null) {
                foto = new UserFoto();
            }
            foto.setRuta(urlCloudinary);
            foto.setNombreArchivo(imagen.getOriginalFilename());
            usuario.setFoto(foto);
        }

        return userRepository.save(usuario);
    }

    // Metodo de eliminar usuario

    @Transactional
    public void eliminarUsuario(Long id, User usuarioAutenticado) {
        User usuario = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        boolean esAdmin = "ADMIN".equals(usuarioAutenticado.getRol());

        if (!esAdmin) {
            throw new RuntimeException("No tienes permisos para eliminar usuarios");
        }

        userRepository.deleteById(id);
    }
}