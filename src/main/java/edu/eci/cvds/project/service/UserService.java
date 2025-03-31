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
            throw new IllegalArgumentException("User already exists");}
        User user = new User();
        user.setUsername(userdto.getUsername());
        user.setRole(userdto.getRole());
        String hashedPassword = passwordEncoder.encode(userdto.getPassword());
        user.setPassword(hashedPassword);
        user.setReservations(new ArrayList<Reservation>());
        return userRepository.saveUser(user);
    }
    @Override
    public User updateAdmin(String username,String token) {
        if (!jwtUtilservice.validateAdmin(token)) {
            throw new IllegalArgumentException("Invalid token");
        }
        User user=userRepository.findUserByUsername(username);
        if(user==null){
            throw new IllegalArgumentException("User not found");
        }
        user.setRole(Role.ADMIN);
        return userRepository.saveUser(user);
    }

    @Override
    public User updateUser(User user) {
        user.setReservations(user.getReservations());
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

    @Override
    public String getRoleByUsername(String username) {
        return userRepository.findUserByUsername(username).getRole().name();
    }
}
