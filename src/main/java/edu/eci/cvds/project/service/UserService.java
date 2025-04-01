package edu.eci.cvds.project.service;
import edu.eci.cvds.project.exception.UserException;
import edu.eci.cvds.project.model.DTO.LaboratoryDTO;
import edu.eci.cvds.project.model.DTO.UserDTO;
import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.model.Reservation;
import edu.eci.cvds.project.model.Role;
import edu.eci.cvds.project.model.User;
import edu.eci.cvds.project.repository.ReservationMongoRepository;
import edu.eci.cvds.project.repository.UserMongoRepository;

import edu.eci.cvds.project.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements ServicesUser {
    @Autowired
    private UserMongoRepository userRepository;
    @Autowired
    private ReservationMongoRepository reservationRepository;
    @Autowired
    private JwtUtil jwtUtilservice;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    /**
     * Guarda un nuevo usuario en el sistema.
     * @param userdto DTO que contiene la información del usuario.
     * @return El usuario guardado.
     */
    @Override
    public User save(UserDTO userdto) {
        if(userRepository.existsByUsername(userdto.getUsername())){
            throw new IllegalArgumentException("User already exists");
        }
        User user = new User();
        user.setUsername(userdto.getUsername());
        user.setRole(userdto.getRole());
        String hashedPassword = passwordEncoder.encode(userdto.getPassword());
        user.setPassword(hashedPassword);
        user.setReservations(new ArrayList<Reservation>());
        return userRepository.saveUser(user);
    }
    /**
     * Método que actualiza el rol de un usuario a "ADMIN" después de validar el token JWT.
     *
     * Este método primero valida si el token JWT proporcionado es válido y corresponde a un administrador.
     * Si el token es válido, busca al usuario por su nombre de usuario. Si el usuario existe, se le asigna el rol
     * de administrador (ADMIN) y se guarda en la base de datos. Si el token es inválido o el usuario no se encuentra,
     * se lanzan excepciones.
     *
     * @param username El nombre de usuario del usuario que se quiere actualizar.
     * @param token El token JWT que se usa para validar la operación.
     * @return El usuario actualizado con el rol ADMIN.
     * @throws IllegalArgumentException Si el token no es válido o el usuario no se encuentra.
     */
    @Override
    public User updateAdmin(String username, String token) {
        // Validar si el token es de un administrador
        if (!jwtUtilservice.validateAdmin(token)) {
            throw new IllegalArgumentException("Invalid token");  // Lanzar excepción si el token no es válido
        }

        // Buscar al usuario por nombre de usuario
        User user = userRepository.findUserByUsername(username);

        // Si el usuario no se encuentra, lanzar una excepción
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Asignar el rol de ADMIN al usuario
        user.setRole(Role.ADMIN);

        // Guardar y devolver el usuario con el rol actualizado
        return userRepository.saveUser(user);
    }

    /**
     * Método que actualiza la información de un usuario.
     *
     * Este método toma un objeto de usuario actualizado, actualiza sus reservas (si las tiene),
     * y luego guarda el usuario con la información actualizada en la base de datos.
     *
     * @param user El objeto de usuario con la información que se desea actualizar.
     * @return El usuario actualizado.
     */
    @Override
    public User updateUser(User user) {
        // Actualizar las reservas del usuario (en este caso parece que el objeto ya tiene las reservas asociadas)
        user.setReservations(user.getReservations());

        // Guardar y devolver el usuario actualizado
        return userRepository.saveUser(user);
    }



    /**
     * Obtiene un usuario por su identificador.
     * @param id Identificador del usuario.
     * @return El usuario correspondiente al ID.
     */
    @Override
    public User getUserById(String id) {
        return userRepository.findUserById(id);
    }

    /**
     * Elimina un usuario por su identificador.
     * @param id Identificador del usuario a eliminar.
     */
    @Override
    public void deleteUser(String id) {
        userRepository.deleteUserById(id);
    }

    /**
     * Obtiene todas las reservas asociadas a un usuario específico.
     * @param id Identificador del usuario.
     * @return Lista de reservas del usuario.
     * @throws RuntimeException Si el usuario no existe.
     */
    @Override
    public List<Reservation> getAllReservationByUserId(String id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Retornar las reservas del usuario
            return user.getReservations();
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }

    }
    /**
     * Obtiene todas las reservas asociadas a un usuario específico.
     * @param username Identificador del usuario.
     * @return Lista de reservas del usuario.
     * @throws RuntimeException Si el usuario no existe.
     */
    @Override
    public List<Reservation> getAllReservationByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user!=null) {
            List<Reservation> reservations = user.getReservations();
            List<Reservation> filteredReservations = new ArrayList<>();
            for (Reservation reservation : reservations) {
                if(reservation.getStatus()==true){
                    filteredReservations.add(reservation);
                }
            }
            return filteredReservations;
        } else {
            throw new RuntimeException("Usuario no encontrado con username: " + username);
        }

    }
    /**
     * Método que verifica las reservas de un usuario y actualiza su estado si la fecha de finalización ya ha pasado.
     *
     * Este método busca al usuario por su nombre de usuario, luego recorre todas las reservas asociadas al usuario.
     * Si alguna de las reservas tiene una fecha de finalización que es anterior a la fecha y hora actuales,
     * se actualiza el estado de la reserva a `false` (indicando que la reserva ya no es válida) y se guardan los cambios
     * en la base de datos. Además, el usuario también es actualizado.
     *
     * @param username El nombre de usuario del usuario cuyas reservas se van a verificar.
     */
    @Override
    public void verifyReservations(String username) {
        User user = userRepository.findUserByUsername(username);
        List<Reservation> reservations = user.getReservations();
        if(reservations != null && !reservations.isEmpty()) {
            for (Reservation reservation : reservations) {
                LocalDateTime end = reservation.getEndDateTime();
                if (end.isBefore(LocalDateTime.now())) {
                    reservation.setStatus(false);
                    reservationRepository.updateReservation(reservation);
                    userRepository.updateUser(user);
                }
            }
        }
    }



    /**
     * Obtiene un usuario por su nombre de usuario.
     * @param username Nombre de usuario.
     * @return El usuario correspondiente al nombre de usuario.
     */
    @Override
    // En tu UserService
    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    /**
     * Obtiene todos los usuarios registrados en el sistema.
     * @return Lista de todos los usuarios.
     */
    @Override
    public List<User> getAllUser() {
        return userRepository.findAllUsers();
    }

    /**
     * Método que obtiene el rol de un usuario a partir de su nombre de usuario.
     *
     * Este método busca al usuario en la base de datos utilizando su nombre de usuario proporcionado,
     * luego obtiene el rol asociado al usuario. El rol es devuelto como un `String` que representa el nombre del rol.
     *
     * @param username El nombre de usuario cuyo rol se desea obtener.
     * @return El nombre del rol del usuario, como un String.
     * @throws NullPointerException Si no se encuentra el usuario o si el rol es nulo.
     */
    @Override
    public String getRoleByUsername(String username) {
        return userRepository.findUserByUsername(username).getRole().name();
    }
}
