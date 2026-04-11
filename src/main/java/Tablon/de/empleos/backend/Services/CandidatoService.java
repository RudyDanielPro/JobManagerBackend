package Tablon.de.empleos.backend.Services;

import Tablon.de.empleos.backend.Entity.Candidato;
import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Entity.UserFoto;
import Tablon.de.empleos.backend.Repository.CandidatoRepository;
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
public class CandidatoService {

    private final CandidatoRepository candidatoRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    public CandidatoService(CandidatoRepository candidatoRepository,
            UserRepository userRepository,
            CloudinaryService cloudinaryService,
            PasswordEncoder passwordEncoder) {
        this.candidatoRepository = candidatoRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Candidato registrarCandidato(User usuario, String nombre, String apellido, MultipartFile imagen)
            throws IOException {
        
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol("CANDIDATO");

    
        User userGuardado = userRepository.save(usuario);

        Candidato candidato = new Candidato(nombre, apellido);
        candidato.setId(userGuardado.getId());
        candidato.setUsuario(userGuardado);

        
        candidato = candidatoRepository.save(candidato);

       
        userGuardado.setCandidato(candidato);
        userRepository.save(userGuardado);

        
        if (imagen != null && !imagen.isEmpty()) {
            try {
                Map result = cloudinaryService.upload(imagen);
                String urlCloudinary = result.get("url").toString();

                UserFoto foto = new UserFoto();
                foto.setRuta(urlCloudinary);
                foto.setNombreArchivo(imagen.getOriginalFilename());
                userGuardado.setFoto(foto);
                userRepository.save(userGuardado);
            } catch (Exception e) {
                System.err.println("Error al subir foto: " + e.getMessage());
            }
        }

        return candidato;
    }

    @Transactional(readOnly = true)
    public Optional<Candidato> buscarPorId(Long id) {
        return candidatoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Candidato> buscarPorUsuarioId(Long userId) {
        return candidatoRepository.findByUsuarioId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Candidato> buscarPorEmail(String email) {
        return candidatoRepository.findByUsuarioEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Candidato> buscarPorNombreUsuario(String nombreUsuario) {
        return candidatoRepository.findByUsuario_Usuario(nombreUsuario);
    }

    @Transactional(readOnly = true)
    public List<Candidato> buscarPorNombre(String nombre) {
        return candidatoRepository.findByNombre(nombre);
    }

    @Transactional(readOnly = true)
    public List<Candidato> buscarPorApellido(String apellido) {
        return candidatoRepository.findByApellido(apellido);
    }

    @Transactional(readOnly = true)
    public List<Candidato> buscarPorNombreYApellido(String nombre, String apellido) {
        return candidatoRepository.findByNombreAndApellido(nombre, apellido);
    }

    @Transactional(readOnly = true)
    public Page<Candidato> buscarTodosPaginado(Pageable pageable) {
        return candidatoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Candidato> buscarTodos() {
        return candidatoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Candidato> buscarTodosConPostulaciones() {
        return candidatoRepository.findAllWithPostulaciones();
    }

    @Transactional(readOnly = true)
    public Optional<Candidato> buscarPorIdConPostulaciones(Long id) {
        return candidatoRepository.findByIdWithPostulaciones(id);
    }

    @Transactional(readOnly = true)
    public Page<Candidato> buscarTodosConPostulacionesPaginado(Pageable pageable) {
        return candidatoRepository.findAllWithPostulaciones(pageable);
    }

    @Transactional
    public Candidato actualizarPerfil(Long id, String nombre, String apellido, User usuarioAutenticado) {
        Candidato candidato = candidatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidato no encontrado con ID: " + id));

        boolean esMismoCandidato = candidato.getUsuario().getId().equals(usuarioAutenticado.getId());
        boolean esAdmin = "ADMIN".equals(usuarioAutenticado.getRol());

        if (!esMismoCandidato && !esAdmin) {
            throw new RuntimeException("No tienes permisos para modificar este candidato");
        }

        if (nombre != null) {
            candidato.setNombre(nombre);
        }
        if (apellido != null) {
            candidato.setApellido(apellido);
        }

        return candidatoRepository.save(candidato);
    }

    @Transactional
    public Candidato actualizarFotoPerfil(Long id, MultipartFile imagen, User usuarioAutenticado) throws IOException {
        Candidato candidato = candidatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidato no encontrado con ID: " + id));

        if (!candidato.getUsuario().getId().equals(usuarioAutenticado.getId())) {
            throw new RuntimeException("No tienes permisos para cambiar la foto de este candidato");
        }

        if (imagen != null && !imagen.isEmpty()) {
            Map result = cloudinaryService.upload(imagen);
            String urlCloudinary = result.get("url").toString();

            User user = candidato.getUsuario();
            UserFoto foto = user.getFoto();
            if (foto == null) {
                foto = new UserFoto();
            }
            foto.setRuta(urlCloudinary);
            foto.setNombreArchivo(imagen.getOriginalFilename());
            user.setFoto(foto);

            userRepository.save(user);
        }

        return candidato;
    }

    @Transactional
    public void eliminarCandidato(Long id, User usuarioAutenticado) {
        Candidato candidato = candidatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidato no encontrado con ID: " + id));

        boolean esAdmin = "ADMIN".equals(usuarioAutenticado.getRol());

        if (!esAdmin) {
            throw new RuntimeException("No tienes permisos para eliminar candidatos");
        }
        userRepository.deleteById(candidato.getUsuario().getId());
    }

    @Transactional(readOnly = true)
    public boolean existeCandidatoPorEmail(String email) {
        return candidatoRepository.findByUsuarioEmail(email).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean existeCandidatoPorUsuario(String usuario) {
        return candidatoRepository.findByUsuario_Usuario(usuario).isPresent();
    }
}