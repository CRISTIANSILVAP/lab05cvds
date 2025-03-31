package edu.eci.cvds.project.service;

import edu.eci.cvds.project.model.DTO.UserDTO;
import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.model.Reservation;
import edu.eci.cvds.project.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;

public interface ServicesUser {

    List<User> getAllUser();
    User getUserByUsername(String username);
    List<Reservation> getAllReservationByUserId(String id);
    User save(UserDTO user);
    User getUserById(String id);
    void deleteUser(String id);
    User updateUser(User user);
    List<Reservation> getAllReservationByUsername(String username);
    void verifyReservations(String username);
    User updateAdmin(String username,String token);
    String getRoleByUsername(String username);


}
