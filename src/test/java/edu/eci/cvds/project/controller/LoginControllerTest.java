package edu.eci.cvds.project.controller;

import edu.eci.cvds.project.exception.UserException;
import edu.eci.cvds.project.service.ServicesLogin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LoginControllerTest {

    @Mock
    private ServicesLogin loginService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Simulación de credenciales
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "testUser");
        credentials.put("password", "testPass");

        // Simulación de la respuesta del servicio
        String expectedToken = "fake-jwt-token";
        when(loginService.loginUser("testUser", "testPass")).thenReturn(expectedToken);

        // Llamado al controlador
        ResponseEntity<String> response = loginController.login(credentials);

        // Verificaciones
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, response.getBody());
        verify(loginService, times(1)).loginUser("testUser", "testPass");
    }

    @Test
    public void testLogin_UserNotFound() throws Exception {
        // Simulación de credenciales incorrectas
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "unknownUser");
        credentials.put("password", "somePass");

        // Simulación de excepción cuando el usuario no existe
        when(loginService.loginUser(anyString(), anyString()))
                .thenThrow(new UserException.UserNotFoundException("User not found"));

        // Llamado al controlador
        ResponseEntity<String> response = loginController.login(credentials);

        // Verificaciones
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(loginService, times(1)).loginUser("unknownUser", "somePass");
    }

    @Test
    public void testLogin_IncorrectPassword() throws Exception {
        // Simulación de credenciales incorrectas
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "testUser");
        credentials.put("password", "wrongPass");

        // Simulación de excepción cuando la contraseña es incorrecta
        when(loginService.loginUser("testUser", "wrongPass"))
                .thenThrow(new UserException.UserIncorrectPasswordException("Incorrect password"));

        // Llamado al controlador
        ResponseEntity<String> response = loginController.login(credentials);

        // Verificaciones
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Incorrect password", response.getBody());
        verify(loginService, times(1)).loginUser("testUser", "wrongPass");
    }

    @Test
    public void testLogin_UnknownError() throws Exception {
        // Simulación de credenciales
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "testUser");
        credentials.put("password", "testPass");

        // Simulación de error inesperado
        when(loginService.loginUser("testUser", "testPass"))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Llamado al controlador
        ResponseEntity<String> response = loginController.login(credentials);

        // Verificaciones
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unknown server error", response.getBody());
        verify(loginService, times(1)).loginUser("testUser", "testPass");
    }
}
