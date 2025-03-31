package edu.eci.cvds.project.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private String secretKey = "secret";

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adminToken = JWT.create()
                .withClaim("role", "ADMIN")
                .sign(Algorithm.HMAC256(secretKey));

        userToken = JWT.create()
                .withClaim("role", "USER")
                .sign(Algorithm.HMAC256(secretKey));
    }

    @Test
    void testValidateAdminWithAdminToken() {
        assertTrue(jwtUtil.validateAdmin(adminToken), "El token de administrador debería ser válido.");
    }

    @Test
    void testValidateAdminWithUserToken() {
        assertFalse(jwtUtil.validateAdmin(userToken), "El token de usuario no debería ser válido como administrador.");
    }

    @Test
    void testValidateAdminWithInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtUtil.validateAdmin(invalidToken), "Un token inválido no debería ser válido.");
    }
}