package edu.eci.cvds.project.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import com.auth0.jwt.interfaces.DecodedJWT;

import com.auth0.jwt.interfaces.JWTVerifier;
import edu.eci.cvds.project.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtUtil {

    private String SECRET_KEY = "secret";


    public boolean validateAdmin(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            // Obtener el rol del token
            String role = decodedJWT.getClaim("role").asString();
            return "ADMIN".equals(role);
        } catch (JWTVerificationException e) {  // Captura todas las excepciones relacionadas con la verificación del token
            return false;
        }
    }

    private DecodedJWT decodeToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).build();
        return verifier.verify(token.replace("Bearer ", ""));

    }


}

