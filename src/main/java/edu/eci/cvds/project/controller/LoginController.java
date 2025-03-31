package edu.eci.cvds.project.controller;

import edu.eci.cvds.project.exception.UserException;
import edu.eci.cvds.project.exception.UserException;
import edu.eci.cvds.project.service.LoginService;
import edu.eci.cvds.project.service.ServicesLogin;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map; // Changed from HashMap to Map

@RestController
@RequestMapping("/api/authenticate")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private ServicesLogin loginService;


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        try {
            // Generación del token
            String token = loginService.loginUser(username, password);
            return ResponseEntity.ok(token);// Devuelve el token al cliente

        } catch (UserException.UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (UserException.UserIncorrectPasswordException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown server error");
        }
    }

}