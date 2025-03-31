package edu.eci.cvds.project.service;

import edu.eci.cvds.project.exception.UserException;
import edu.eci.cvds.project.model.DTO.UserDTO;
import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.model.Reservation;
import edu.eci.cvds.project.model.User;
import edu.eci.cvds.project.model.Role;
import edu.eci.cvds.project.repository.ReservationMongoRepository;
import edu.eci.cvds.project.repository.UserMongoRepository;
import edu.eci.cvds.project.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMongoRepository userRepository;

    @Mock
    private ReservationMongoRepository reservationRepository;

    @Mock
    private JwtUtil jwtUtilservice;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private ReservationService reservationService;

    private User user;
    private UserDTO userDTO;
    private Reservation reservation;
    private Laboratory laboratory;

    @BeforeEach
    void setUp() {
        ArrayList<Reservation> reservations = new ArrayList<>();
        laboratory = new Laboratory("1", "Laboratory1", reservations);
        user = new User("100011", "Miguel", "password", reservations, Role.USER);
        LocalDateTime start = LocalDateTime.of(2025, 3, 10, 22, 0);
        LocalDateTime end = LocalDateTime.of(2025, 3, 10, 23, 0);
        reservation = new Reservation("10222", laboratory.getName(), user.getUsername(), start, end, "nose", true, 4);
        reservations.add(reservation);

        userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
    }

    @Test
    void testGetUserById() {
        when(userRepository.findUserById("100011")).thenReturn(user);
        User foundUser = userService.getUserById("100011");
        assertNotNull(foundUser);
        assertEquals("Miguel", foundUser.getUsername());
        verify(userRepository, times(1)).findUserById("100011");
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteUserById("100011");
        assertDoesNotThrow(() -> userService.deleteUser("100011"));
        verify(userRepository, times(1)).deleteUserById("100011");
    }

    @Test
    void testGetUserByUsername() {
        when(userRepository.findUserByUsername("Miguel")).thenReturn(user);
        User foundUser = userService.getUserByUsername("Miguel");
        assertNotNull(foundUser);
        assertEquals("Miguel", foundUser.getUsername());
        verify(userRepository, times(1)).findUserByUsername("Miguel");
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAllUsers()).thenReturn(Collections.singletonList(user));
        List<User> users = userService.getAllUser();
        assertEquals(1, users.size());
        assertEquals("Miguel", users.get(0).getUsername());
        verify(userRepository, times(1)).findAllUsers();
    }

    @Test
    void testUpdateUser() {
        List<Reservation> updatedReservations = new ArrayList<>();
        User updatedUser = new User("100011", "Miguel", "newpassword", updatedReservations, Role.ADMIN);
        when(userRepository.saveUser(any(User.class))).thenReturn(updatedUser);
        User result = userService.updateUser(updatedUser);
        assertNotNull(result);
        assertEquals("Miguel", result.getUsername());
        assertEquals(Role.ADMIN, result.getRole());
        assertEquals("newpassword", result.getPassword());
        assertEquals(updatedReservations, result.getReservations());
        verify(userRepository, times(1)).saveUser(updatedUser);
    }

    @Test
    void testGetAllReservationByUserId_UserExists() {
        when(userRepository.findById("100011")).thenReturn(Optional.of(user));
        List<Reservation> reservations = userService.getAllReservationByUserId("100011");
        assertNotNull(reservations);
        assertEquals(1, reservations.size());
        assertEquals(reservation, reservations.get(0));
        verify(userRepository, times(1)).findById("100011");
    }

    @Test
    void testGetAllReservationByUserId_UserNotExists() {
        when(userRepository.findById("99999")).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getAllReservationByUserId("99999"));
        assertEquals("Usuario no encontrado con ID: 99999", exception.getMessage());
        verify(userRepository, times(1)).findById("99999");
    }

    @Test
    void shouldThrowExceptionIfUserAlreadyExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("Miguel"); // Asegurar que coincida con el código en UserService
        userDTO.setPassword("password123");
        userDTO.setRole(Role.USER);

        when(userRepository.existsByUsername("Miguel")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.save(userDTO);
        });

        assertEquals("User already exists", exception.getMessage());

        verify(userRepository, times(1)).existsByUsername("Miguel");
    }

    @Test
    void testGetAllReservationByUsername_UserHasActiveReservations() {
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(user);

        List<Reservation> result = userService.getAllReservationByUsername(user.getUsername());

        assertFalse(result.isEmpty());
        assertEquals(1, result.size()); // Debe devolver solo la activa
        verify(userRepository, times(1)).findUserByUsername(user.getUsername());
    }

    @Test
    void testGetAllReservationByUsername_UserHasOnlyCanceledReservations() {
        reservation.setStatus(false);
        user.setReservations(List.of(reservation));

        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(user);

        List<Reservation> result = userService.getAllReservationByUsername(user.getUsername());

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllReservationByUsername_UserNotFound() {
        when(userRepository.findUserByUsername("no-existe")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.getAllReservationByUsername("no-existe")
        );

        assertEquals("Usuario no encontrado con username: no-existe", exception.getMessage());
    }



    @Test
    void testUpdateAdmin_Success() {
        when(jwtUtilservice.validateAdmin("validToken")).thenReturn(true);
        when(userRepository.findUserByUsername("Miguel")).thenReturn(user);
        when(userRepository.saveUser(any(User.class))).thenReturn(user);
        User updatedUser = userService.updateAdmin("Miguel", "validToken");
        assertEquals(Role.ADMIN, updatedUser.getRole());
        verify(userRepository, times(1)).saveUser(user);
    }

    @Test
    void testUpdateAdmin_UserNotFound() {
        when(jwtUtilservice.validateAdmin("validToken")).thenReturn(true);
        when(userRepository.findUserByUsername("unknown")).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> userService.updateAdmin("unknown", "validToken"));
    }

    @Test
    void testUpdateAdmin_InvalidToken() {
        when(jwtUtilservice.validateAdmin("invalidToken")).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> userService.updateAdmin("Miguel", "invalidToken"));
    }

    @Test
    void testVerifyReservations() {
        when(userRepository.findUserByUsername("Miguel")).thenReturn(user);
        userService.verifyReservations("Miguel");
        verify(reservationRepository, times(1)).updateReservation(any(Reservation.class));
    }

    @Test
    void testGetRoleByUsername() {
        when(userRepository.findUserByUsername("Miguel")).thenReturn(user);
        assertEquals("USER", userService.getRoleByUsername("Miguel"));
    }
}
