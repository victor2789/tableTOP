package com.TableTOP.api.Usuario.UsuarioRepository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.TableTOP.api.Usuario.Entity.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
