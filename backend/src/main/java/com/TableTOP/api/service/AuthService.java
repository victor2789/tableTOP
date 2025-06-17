package com.TableTOP.api.service;

import java.time.LocalDate;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.TableTOP.api.config.ContrasennaIncorrectaException;
import com.TableTOP.api.config.UsuarioNoEncontradoException;
import com.TableTOP.api.config.EmailYaRegistradoException;
import com.TableTOP.api.dto.AuthRequestDTO;
import com.TableTOP.api.dto.AuthResponseDTO;
import com.TableTOP.api.dto.RegisterRequestDTO;
import com.TableTOP.api.model.Usuario;
import com.TableTOP.api.repository.UsuarioRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       @Value("${jwt.secret}") String jwtSecret) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtSecret = jwtSecret;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .build();
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();

        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new EmailYaRegistradoException("Email ya registrado");
        }

        String id;
        try {
            id = "" + (Long.parseLong(usuarioRepository.findTopByOrderByIdDesc().map(Usuario::getId).orElse("0")) + 1);
        } catch(Exception e) {
            id = "1";
        }

        String username = request.getUsername();
        String password = passwordEncoder.encode(request.getPassword());
        LocalDate fechaCreacion = LocalDate.now();

        Usuario usuario = new Usuario(id, username, email, password, fechaCreacion);
        usuarioRepository.save(usuario);

        String token = generateToken(usuario);
        return new AuthResponseDTO(token, usuario.getId(), usuario.getUsername());
    }


    public AuthResponseDTO login(AuthRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail().trim().toLowerCase())
            .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new ContrasennaIncorrectaException("Contraseña incorrecta");
        }

        String token = generateToken(usuario);
        return new AuthResponseDTO(token, usuario.getId(), usuario.getUsername());
    }


    private String generateToken(Usuario usuario) {
    	byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    	SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        return Jwts.builder()
                .setSubject(usuario.getId())
                .claim("username", usuario.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*3))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
