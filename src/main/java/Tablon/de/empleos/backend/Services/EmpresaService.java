package Tablon.de.empleos.backend.Services;

import Tablon.de.empleos.backend.Entity.Empresa;
import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    // 1. Obtener todas las empresas (Solo para el ADMIN o el Tablón general)
    @Transactional(readOnly = true)
    public List<Empresa> obtenerTodas() {
        return empresaRepository.findAll();
    }

    // 2. Obtener la empresa por su ID
    @Transactional(readOnly = true)
    public Optional<Empresa> obtenerPorId(Long id) {
        return empresaRepository.findById(id);
    }

    // 3. Obtener la empresa vinculada a un Usuario (Clave para el Login)
    @Transactional(readOnly = true)
    public Optional<Empresa> obtenerPorUsuario(Long usuarioId) {
        return empresaRepository.findByUsuarioId(usuarioId);
    }

    // 4. Crear o Actualizar Empresa
    @Transactional
    public Empresa guardar(Empresa empresa) {
        // Aquí podrías agregar validaciones, como verificar que el correoContacto sea válido
        return empresaRepository.save(empresa);
    }

    // 5. Actualización parcial (Para el perfil de la empresa)
    @Transactional
    public Empresa actualizarPerfil(Long id, Empresa datosNuevos, User usuarioAutenticado) {
        return empresaRepository.findById(id).map(empresa -> {
            
            // SEGURIDAD: Solo el ADMIN o el dueño de la empresa pueden editar
            boolean esAdmin = usuarioAutenticado.getRol().equals("ROLE_ADMIN");
            boolean esDueno = empresa.getUsuario().getId().equals(usuarioAutenticado.getId());

            if (esAdmin || esDueno) {
                empresa.setNombre(datosNuevos.getNombre());
                empresa.setDescripcion(datosNuevos.getDescripcion());
                empresa.setCorreoContacto(datosNuevos.getCorreoContacto());
                empresa.setUrl(datosNuevos.getUrl());
                // Si viene una foto nueva, se actualiza aquí también
                if (datosNuevos.getFoto() != null) {
                    empresa.setFoto(datosNuevos.getFoto());
                }
                return empresaRepository.save(empresa);
            } else {
                throw new RuntimeException("No tienes permisos para modificar esta empresa.");
            }
        }).orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + id));
    }

    // 6. Eliminar Empresa (Poder exclusivo del ADMIN)
    @Transactional
    public void eliminar(Long id) {
        empresaRepository.deleteById(id);
    }
}