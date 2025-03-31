package edu.eci.cvds.project.controller;

import edu.eci.cvds.project.model.DTO.UserDTO;
import edu.eci.cvds.project.model.Reservation;
import edu.eci.cvds.project.model.Role;
import edu.eci.cvds.project.model.User;
import edu.eci.cvds.project.service.ServicesUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private ServicesUser userService;

    @InjectMocks
    private UserController userController;

    @Test
    public void testGetAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("1", "user1", "pwd1", new ArrayList<>(),  Role.USER));
        users.add(new User("2", "admin1", "pwd2", new ArrayList<>() ,Role.ADMIN));
        when(userService.getAllUser()).thenReturn(users);

        List<User> result = userController.getAllUsers();

        assertEquals(users, result);
        verify(userService, times(1)).getAllUser();
    }

    @Test
    public void testSaveUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        User savedUser = new User("3", "newuser", "pwd", new ArrayList<>(), Role.USER);
        when(userService.save(userDTO)).thenReturn(savedUser);

        ResponseEntity<?> response = userController.saveUser(userDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedUser, response.getBody());
        verify(userService, times(1)).save(userDTO);
    }

    @Test
    public void testSaveUser_Error() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("newuser");
        when(userService.save(userDTO)).thenThrow(new RuntimeException("Error saving user"));

        ResponseEntity<?> response = userController.saveUser(userDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error saving user", ((HashMap<?, ?>) response.getBody()).get("error"));
        verify(userService, times(1)).save(userDTO);
    }

//    @Test
//    public void testSaveAdmin_Success() {
//        UserDTO userDTO = new UserDTO();
//        userDTO.setUsername("newadmin");
//        User savedUser = new User("4", "newadmin", "pwd", new ArrayList<>(), Role.ADMIN);
//        when(userService.save(userDTO)).thenReturn(savedUser);
//
    ////        ResponseEntity<?> response = userController.saveAdmin(userDTO.getUsername());
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(savedUser, response.getBody());
//        verify(userService, times(1)).save(userDTO);
//    }

//    @Test
//    public void testSaveAdmin_Error() {
//        UserDTO userDTO = new UserDTO();
//        userDTO.setUsername("newadmin");
//        when(userService.save(userDTO)).thenThrow(new RuntimeException("Error saving admin"));
//
//        ResponseEntity<?> response = userController.saveAdmin(userDTO.getUsername());
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Error saving admin", ((HashMap<?, ?>) response.getBody()).get("error"));
//        verify(userService, times(1)).save(userDTO);
//    }

    @Test
    public void testGetUserById_Success() {
        User user = new User("1", "user1", "pwd1", new ArrayList<>(), Role.USER);
        when(userService.getUserById("1")).thenReturn(user);

        ResponseEntity<?> response = userController.getUserById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).getUserById("1");
    }

    @Test
    public void testGetUserById_Error() {
        when(userService.getUserById("5")).thenThrow(new RuntimeException("Error getting user"));

        ResponseEntity<?> response = userController.getUserById("5");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error getting user", ((HashMap<?, ?>) response.getBody()).get("error"));
        verify(userService, times(1)).getUserById("5");
    }

    @Test
    public void testDeleteUser_Success() {
        doNothing().when(userService).deleteUser("1");

        ResponseEntity<?> response = userController.deleteUser("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("1", ((HashMap<?, ?>) response.getBody()).get("user-delete"));
        verify(userService, times(1)).deleteUser("1");
    }

    @Test
    public void testDeleteUser_Error() {
        doThrow(new RuntimeException("Error deleting user")).when(userService).deleteUser("6");

        ResponseEntity<?> response = userController.deleteUser("6");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error deleting user", ((HashMap<?, ?>) response.getBody()).get("error"));
        verify(userService, times(1)).deleteUser("6");
    }

    @Test
    public void testGetAllReservationByUserId_Success() {
        List<Reservation> reservations = new ArrayList<>();
        when(userService.getAllReservationByUserId("1")).thenReturn(reservations);

        ResponseEntity<?> response = userController.getAllReservationByUserId("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservations, response.getBody());
        verify(userService, times(1)).getAllReservationByUserId("1");
    }

    @Test
    public void testGetAllReservationByUserId_Error() {
        when(userService.getAllReservationByUserId("7")).thenThrow(new RuntimeException("Error getting reservations"));

        ResponseEntity<?> response = userController.getAllReservationByUserId("7");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error getting reservations", ((HashMap<?, ?>) response.getBody()).get("error"));
        verify(userService, times(1)).getAllReservationByUserId("7");
    }

    @Test
    public void testUpdateUser_Success() {
        User user = new User("1", "updatedUser", "newPwd", new ArrayList<>(), Role.USER);
        when(userService.updateUser(user)).thenReturn(user);

        ResponseEntity<?> response = userController.updateReservation(user);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).updateUser(user);
    }

    @Test
    public void testUpdateUser_Error() {
        User user = new User("1", "updatedUser", "newPwd", new ArrayList<>(), Role.USER);
        when(userService.updateUser(user)).thenThrow(new RuntimeException("Error updating user"));

        ResponseEntity<?> response = userController.updateReservation(user);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error updating user", ((HashMap<?, ?>) response.getBody()).get("error"));
        verify(userService, times(1)).updateUser(user);
    }

    @Test
    public void testGetUserByUsername_Success() {
        User user = new User("1", "user1", "pwd1", new ArrayList<>(), Role.USER);
        when(userService.getUserByUsername("user1")).thenReturn(user);

        ResponseEntity<?> response = userController.getUserByUsername("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService, times(1)).getUserByUsername("user1");
    }

    @Test
    public void testGetUserByUsername_Error() {
        when(userService.getUserByUsername("unknown")).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<?> response = userController.getUserByUsername("unknown");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found", ((HashMap<?, ?>) response.getBody()).get("error"));
        verify(userService, times(1)).getUserByUsername("unknown");
    }

    @Test
    public void testGetUserRole_Success() {
        when(userService.getRoleByUsername("user1")).thenReturn("ROLE_USER");

        ResponseEntity<String> response = userController.getUserRole("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ROLE_USER", response.getBody());
        verify(userService, times(1)).getRoleByUsername("user1");
    }

    @Test
    public void testGetUserRole_Error() {
        when(userService.getRoleByUsername("unknown")).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<String> response = userController.getUserRole("unknown");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(userService, times(1)).getRoleByUsername("unknown");
    }

    @Test
    public void testGetAllReservationByUsername_Success() {
        List<Reservation> reservations = new ArrayList<>();
        when(userService.getAllReservationByUsername("user1")).thenReturn(reservations);

        ResponseEntity<?> response = userController.getAllReservationByUsername("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservations, response.getBody());
        verify(userService, times(1)).getAllReservationByUsername("user1");
    }

    @Test
    public void testGetAllReservationByUsername_Error() {
        when(userService.getAllReservationByUsername("unknown")).thenThrow(new RuntimeException("Error fetching reservations"));

        ResponseEntity<?> response = userController.getAllReservationByUsername("unknown");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error fetching reservations", ((HashMap<?, ?>) response.getBody()).get("error"));
        verify(userService, times(1)).getAllReservationByUsername("unknown");
    }

    @Test
    public void testSaveAdmin_Success() {
        String token = "valid-token";
        User updatedUser = new User("2", "adminUser", "pwd", new ArrayList<>(), Role.ADMIN);
        when(userService.updateAdmin("adminUser", token)).thenReturn(updatedUser);

        ResponseEntity<?> response = userController.saveAdmin("adminUser", token);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
        verify(userService, times(1)).updateAdmin("adminUser", token);
    }

    @Test
    public void testSaveAdmin_Error() {
        String token = "invalid-token";
        when(userService.updateAdmin("adminUser", token)).thenThrow(new RuntimeException("Unauthorized"));

        ResponseEntity<?> response = userController.saveAdmin("adminUser", token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unauthorized", ((HashMap<?, ?>) response.getBody()).get("error"));
        verify(userService, times(1)).updateAdmin("adminUser", token);
    }
}