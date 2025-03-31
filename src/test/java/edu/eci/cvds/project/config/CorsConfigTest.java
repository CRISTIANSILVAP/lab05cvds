package edu.eci.cvds.project.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CorsConfigTest {

    @InjectMocks
    private CorsConfig corsConfig;

    private CorsRegistry corsRegistry;

    @BeforeEach
    void setUp() {
        corsRegistry = new CorsRegistry();
        corsConfig.addCorsMappings(corsRegistry);
    }

    @Test
    void testCorsMappings() {
        assertNotNull(corsRegistry);
        assertDoesNotThrow(() -> corsConfig.addCorsMappings(corsRegistry));
    }
}