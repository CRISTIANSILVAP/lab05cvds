package edu.eci.cvds.project.service;

import edu.eci.cvds.project.exception.UserException;
import edu.eci.cvds.project.model.Role;
import edu.eci.cvds.project.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private LoginService loginService;

    private BCryptPasswordEncoder passwordEncoder;
    private User user;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode("password123"); // Simulación de contraseña encriptada

        user = new User("100011", "Miguel", hashedPassword, null, Role.USER);
    }

    @Test
    void testLoginUser_Success() throws UserException.UserNotFoundException, UserException.UserIncorrectPasswordException {
        when(userService.getUserByUsername("Miguel")).thenReturn(user);

        String token = loginService.loginUser("Miguel", "password123");

        assertNotNull(token);
        assertFalse(token.isEmpty());

        verify(userService, times(1)).getUserByUsername("Miguel");
    }

    @Test
    void testLoginUser_UserNotFound() {
        when(userService.getUserByUsername("Miguel")).thenReturn(null);

        UserException.UserNotFoundException exception = assertThrows(
                UserException.UserNotFoundException.class,
                () -> loginService.loginUser("Miguel", "password123")
        );

        assertEquals("User not found", exception.getMessage());

        verify(userService, times(1)).getUserByUsername("Miguel");
    }

    @Test
    void testLoginUser_IncorrectPassword() {
        when(userService.getUserByUsername("Miguel")).thenReturn(user);

        UserException.UserIncorrectPasswordException exception = assertThrows(
                UserException.UserIncorrectPasswordException.class,
                () -> loginService.loginUser("Miguel", "wrongpassword")
        );

        assertEquals("Incorrect password", exception.getMessage());

        verify(userService, times(1)).getUserByUsername("Miguel");
    }
}