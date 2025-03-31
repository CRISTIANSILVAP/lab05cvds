package edu.eci.cvds.project.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private JwtRequestFilter jwtRequestFilter;

    @Mock
    private HttpSecurity httpSecurity;

    @BeforeEach
    void setUp() {
        try {
            // Simula el comportamiento de HttpSecurity para evitar NullPointerException
            when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
            when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);

            // Aquí usamos DefaultSecurityFilterChain en lugar de SecurityFilterChain
            when(httpSecurity.build()).thenReturn(mock(DefaultSecurityFilterChain.class));
        } catch (Exception e) {
            fail("Exception thrown during setup: " + e.getMessage());
        }
    }

    //@Test
    //void testSecurityFilterChain() {
    //    try {
    //       SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity, jwtRequestFilter);
    //        assertNotNull(filterChain);
    //    } catch (Exception e) {
    //        fail("Exception thrown during testSecurityFilterChain: " + e.getMessage());
    //    }
    //}
}
