package Tablon.de.empleos.backend.Services;

import Tablon.de.empleos.backend.Entity.Empresa;
import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Entity.UserFoto;
import Tablon.de.empleos.backend.Repository.EmpresaRepository;
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
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    public EmpresaService(EmpresaRepository empresaRepository,
            UserRepository userRepository,
            CloudinaryService cloudinaryService,
            PasswordEncoder passwordEncoder) {
        this.empresaRepository = empresaRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Empresa registrarEmpresa(User usuario, String nombreEmpresa, String descripcion, String url,
            MultipartFile logo) throws IOException {

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol("RECRUITER");

        Empresa empresa = new Empresa(nombreEmpresa, descripcion, url);
        empresa.setUsuario(usuario); 
        usuario.setEmpresa(empresa);

        User userGuardado = userRepository.save(usuario);

        // 4. Manejo del logo (si existe)
        if (logo != null && !logo.isEmpty()) {
            try {
                Map result = cloudinaryService.upload(logo);
                String urlCloudinary = result.get("url").toString();
                UserFoto foto = new UserFoto();
                foto.setRuta(urlCloudinary);
                foto.setNombreArchivo(logo.getOriginalFilename());
                userGuardado.setFoto(foto);
                userRepository.save(userGuardado);
            } catch (Exception e) {
                System.err.println("Error al subir logo: " + e.getMessage());
            }
        }

        return userGuardado.getEmpresa();
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> buscarPorId(Long id) {
        return empresaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> buscarPorUsuarioId(Long userId) {
        return empresaRepository.findByUsuarioId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> buscarPorEmail(String email) {
        return empresaRepository.findByUsuarioEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> buscarPorNombreEmpresa(String nombreEmpresa) {
        return empresaRepository.findByNombreEmpresa(nombreEmpresa);
    }

    @Transactional(readOnly = true)
    public Page<Empresa> buscarPorNombreContaining(String keyword, Pageable pageable) {
        return empresaRepository.findByNombreEmpresaContaining(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public List<Empresa> buscarPorNombreContaining(String keyword) {
        return empresaRepository.findByNombreEmpresaContaining(keyword);
    }

    @Transactional(readOnly = true)
    public Page<Empresa> buscarTodosPaginado(Pageable pageable) {
        return empresaRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Empresa> buscarTodos() {
        return empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Empresa> buscarTodosConOfertas() {
        return empresaRepository.findAllWithOfertas();
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> buscarPorIdConOfertas(Long id) {
        return empresaRepository.findByIdWithOfertas(id);
    }

    @Transactional(readOnly = true)
    public Page<Empresa> buscarTodosConOfertasPaginado(Pageable pageable) {
        return empresaRepository.findAllWithOfertas(pageable);
    }

    @Transactional
    public Empresa actualizarPerfil(Long id, String nombreEmpresa, String descripcion, String url,
            User usuarioAutenticado) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + id));

        boolean esAdmin = "ADMIN".equals(usuarioAutenticado.getRol());
        boolean esDueno = empresa.getUsuario().getId().equals(usuarioAutenticado.getId());

        if (!esAdmin && !esDueno) {
            throw new RuntimeException("No tienes permisos para modificar esta empresa");
        }

        if (nombreEmpresa != null) {
            empresa.setNombreEmpresa(nombreEmpresa);
        }
        if (descripcion != null) {
            empresa.setDescripcion(descripcion);
        }
        if (url != null) {
            empresa.setUrl(url);
        }

        return empresaRepository.save(empresa);
    }

    @Transactional
    public Empresa actualizarLogo(Long id, MultipartFile logo, User usuarioAutenticado) throws IOException {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + id));

        boolean esAdmin = "ADMIN".equals(usuarioAutenticado.getRol());
        boolean esDueno = empresa.getUsuario().getId().equals(usuarioAutenticado.getId());

        if (!esAdmin && !esDueno) {
            throw new RuntimeException("No tienes permisos para cambiar el logo de esta empresa");
        }

        if (logo != null && !logo.isEmpty()) {
            Map result = cloudinaryService.upload(logo);
            String urlCloudinary = result.get("url").toString();

            User user = empresa.getUsuario();
            UserFoto foto = user.getFoto();
            if (foto == null) {
                foto = new UserFoto();
            }
            foto.setRuta(urlCloudinary);
            foto.setNombreArchivo(logo.getOriginalFilename());
            user.setFoto(foto);

            userRepository.save(user);
        }

        return empresa;
    }

    @Transactional
    public Empresa actualizarEmailContacto(Long id, String email, User usuarioAutenticado) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + id));

        boolean esAdmin = "ADMIN".equals(usuarioAutenticado.getRol());
        boolean esDueno = empresa.getUsuario().getId().equals(usuarioAutenticado.getId());

        if (!esAdmin && !esDueno) {
            throw new RuntimeException("No tienes permisos para cambiar el email de esta empresa");
        }

        User user = empresa.getUsuario();
        user.setEmail(email);
        userRepository.save(user);

        return empresa;
    }

    @Transactional
    public void eliminarEmpresa(Long id, User usuarioAutenticado) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + id));

        boolean esAdmin = "ADMIN".equals(usuarioAutenticado.getRol());

        if (!esAdmin) {
            throw new RuntimeException("No tienes permisos para eliminar empresas");
        }

        userRepository.deleteById(empresa.getUsuario().getId());
    }

    @Transactional(readOnly = true)
    public boolean existeEmpresaPorEmail(String email) {
        return empresaRepository.findByUsuarioEmail(email).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean existeEmpresaPorNombre(String nombreEmpresa) {
        return empresaRepository.findByNombreEmpresa(nombreEmpresa).isPresent();
    }
}