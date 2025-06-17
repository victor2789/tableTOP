package com.TableTOP.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.TableTOP.api.dto.AuthRequestDTO;
import com.TableTOP.api.dto.AuthResponseDTO;
import com.TableTOP.api.dto.RegisterRequestDTO;
import com.TableTOP.api.model.Usuario;
import com.TableTOP.api.service.AuthService;
import com.TableTOP.api.service.JwtService;
import com.TableTOP.api.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class AuthController {


	public AuthController(AuthService authService, UsuarioService usuarioService, JwtService jwtService) {
		super();
		this.authService = authService;
		this.usuarioService = usuarioService;
		this.jwtService = jwtService;
	}

	private final AuthService authService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> getPerfil(HttpServletRequest request) {
        String token = jwtService.extractToken(request);
        String username = jwtService.extractUsername(token);
        Optional<Usuario> usuarioOpt = usuarioService.getUsuarioByUsername(username);

        if (usuarioOpt.isEmpty()) return ResponseEntity.status(404).body("Usuario no encontrado");

        Usuario usuario = usuarioOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("id", usuario.getId());
        response.put("username", usuario.getUsername());
        response.put("historial", usuario.getHistorial());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{id}")
    public ResponseEntity<String> getUsernameById(@PathVariable String id) {
        return usuarioService.getUsuarioById(id)
                .map(user -> ResponseEntity.ok(user.getUsername()))
                .orElse(ResponseEntity.status(404).body("Usuario no encontrado"));
    }

    @PostMapping("/usernames")
    public ResponseEntity<Map<String, String>> getUsernamesFromIds(@RequestBody List<String> ids) {
        Map<String, String> map = ids.stream()
                .map(id -> usuarioService.getUsuarioById(id)
                        .map(u -> Map.entry(id, u.getUsername()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return ResponseEntity.ok(map);
    }
}
