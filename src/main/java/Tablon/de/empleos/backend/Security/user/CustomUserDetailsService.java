package Tablon.de.empleos.backend.Security.user;

import Tablon.de.empleos.backend.Entity.User;
import Tablon.de.empleos.backend.Repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identificador) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(identificador)
                .orElseGet(() -> userRepository.findByUsuario(identificador)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + identificador)));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRol().toUpperCase())));
    }
}