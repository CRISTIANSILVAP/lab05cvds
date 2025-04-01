package edu.eci.cvds.project.service;

import edu.eci.cvds.project.model.User;
import edu.eci.cvds.project.repository.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMongoRepository userRepository;

    /**
     * Método que carga los detalles del usuario a partir de su nombre de usuario.
     *
     * Este método es parte del proceso de autenticación en Spring Security. Se utiliza para cargar un usuario desde la base de datos
     * según su nombre de usuario (username) y luego retornar los detalles del usuario (incluyendo su contraseña y roles) necesarios
     * para la autenticación. Si no se encuentra un usuario con el nombre de usuario proporcionado, se lanza una excepción.
     *
     * @param username El nombre de usuario del que se quiere cargar los detalles.
     * @return Un objeto UserDetails que contiene los detalles del usuario, incluyendo nombre de usuario, contraseña y roles.
     * @throws UsernameNotFoundException Si no se encuentra un usuario con el nombre de usuario proporcionado.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con el nombre de usuario: " + username);
        }
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
