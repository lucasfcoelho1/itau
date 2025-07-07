package com.coelho.desafio.itau.controller;

import com.coelho.desafio.itau.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    //TOD: Melhor tratamento de auth com db, login e usuarios
    @PostMapping("/token")
    public ResponseEntity<?> generateToken(
            @RequestParam String user,
            @RequestParam String password
    ) {
        if (isValidUser(user, password)) {
            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário ou senha inválidos");
        }
    }

    private boolean isValidUser(String user, String password) {
        return "admin".equals(user) && "1234".equals(password);
    }
}