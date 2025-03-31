package edu.eci.cvds.project.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import edu.eci.cvds.project.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws ServletException, IOException {
//
//        // Obtener el header de la autorización
//        final String authorizationHeader = request.getHeader("Authorization");
//        String username = null;
//        String jwtToken = null;
//
//        // Verificar si el header contiene el token "Bearer"
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            jwtToken = authorizationHeader.substring(7); // Extraer el token JWT (sin "Bearer")
//            try {
//                // Verificar el token usando la clave secreta
//                DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256("secret")).build().verify(jwtToken);
//                username = decodedJWT.getClaim("username").asString(); // Obtener el nombre de usuario del token
//            } catch (JWTVerificationException e) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Token inválido: " + e.getMessage());
//                return;
//            }
//
//        }
//
//        // Si el nombre de usuario no es nulo y no hay autenticación previa, configurar la autenticación
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            try {
//                // Cargar los detalles del usuario (asegurándote de que el usuario exista)
//                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//
//                // Verificar si el token es válido y configurar la autenticación
//                if (userDetails != null) {
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                            userDetails, null, userDetails.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authentication); // Establecer la autenticación
//                }
//            } catch (JWTVerificationException e) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Token inválido: " + e.getMessage());
//                return;
//            }
//
//        }
//        // Continuar con el siguiente filtro en la cadena
//        chain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            try {
                DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256("secret")).build().verify(jwtToken);
                username = decodedJWT.getClaim("username").asString();
            } catch (JWTVerificationException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido: " + e.getMessage());
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                             userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }

        chain.doFilter(request, response);
    }
}
