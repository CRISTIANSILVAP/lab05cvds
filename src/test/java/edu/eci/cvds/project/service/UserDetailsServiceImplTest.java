package edu.eci.cvds.project.service;

import edu.eci.cvds.project.model.User;
import edu.eci.cvds.project.model.Role;
import edu.eci.cvds.project.repository.UserMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDetailsServiceImplTest {

    @Mock
    private UserMongoRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User("1","testUser", "password123",new ArrayList<>(), Role.USER);
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        when(userRepository.findUserByUsername("testUser")).thenReturn(testUser);

        UserDetails userDetails = userDetailsService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        verify(userRepository, times(1)).findUserByUsername("testUser");
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findUserByUsername("unknownUser")).thenReturn(null);

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknownUser");
        });

        assertEquals("Usuario no encontrado con el nombre de usuario: unknownUser", exception.getMessage());
        verify(userRepository, times(1)).findUserByUsername("unknownUser");
    }
}

