package com.TableTOP.api.Usuario.UsuarioRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TableTOP.api.Usuario.Entity.Usuario;

import com.TableTOP.api.Usuario.UsuarioService.*;
import com.TableTOP.api.Usuario.dto.AuthResponse;
import com.TableTOP.api.Usuario.dto.LoginRequest;
import com.TableTOP.api.Usuario.dto.RegisterRequest;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsuarioRestController {
    private final UsuarioService userService;

    public UsuarioRestController(UsuarioService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(UsuarioService.login(request));
    }
}

