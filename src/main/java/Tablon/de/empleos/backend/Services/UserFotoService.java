package Tablon.de.empleos.backend.Services;

import Tablon.de.empleos.backend.Entity.UserFoto;
import Tablon.de.empleos.backend.Repository.UserFotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserFotoService {

    private final UserFotoRepository userFotoRepository;

    public UserFotoService(UserFotoRepository userFotoRepository) {
        this.userFotoRepository = userFotoRepository;
    }

    @Transactional
    public UserFoto guardarFoto(UserFoto foto) {
        return userFotoRepository.save(foto);
    }

    @Transactional(readOnly = true)
    public Optional<UserFoto> buscarPorId(Long id) {
        return userFotoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<UserFoto> buscarPorRuta(String ruta) {
        return userFotoRepository.findByRuta(ruta);
    }

    @Transactional(readOnly = true)
    public List<UserFoto> buscarPorNombreArchivo(String nombreArchivo) {
        return userFotoRepository.findByNombreArchivo(nombreArchivo);
    }

    @Transactional
    public UserFoto actualizarFoto(Long id, String nuevaRuta, String nuevoNombreArchivo) {
        UserFoto foto = userFotoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Foto no encontrada con ID: " + id));

        if (nuevaRuta != null) {
            foto.setRuta(nuevaRuta);
        }
        if (nuevoNombreArchivo != null) {
            foto.setNombreArchivo(nuevoNombreArchivo);
        }

        return userFotoRepository.save(foto);
    }

    @Transactional
    public void eliminarFoto(Long id) {
        if (!userFotoRepository.existsById(id)) {
            throw new RuntimeException("Foto no encontrada con ID: " + id);
        }
        userFotoRepository.deleteById(id);
    }
}