package com.TableTOP.api.service;



import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.TableTOP.api.model.Usuario;
import com.TableTOP.api.repository.UsuarioRepository;

@Service
public class UsuarioService {

    public UsuarioService(UsuarioRepository usuarioRepository) {
		super();
		this.usuarioRepository = usuarioRepository;
	}
	private final UsuarioRepository usuarioRepository;

    public Optional<Usuario> getUsuarioById(String id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public void agregarAPartida(String userId, String partidaId) {
        usuarioRepository.findById(userId).ifPresent(usuario -> {
            List<String> historial = usuario.getHistorial();
            if (!historial.contains(partidaId)) {
                historial.add(partidaId);
                usuario.setHistorial(historial);
                usuarioRepository.save(usuario);
            }
        });
    }
    public String obtenerUsuarioConIdMasGrande() {
        Usuario usuario = usuarioRepository.findTopByOrderByIdDesc().orElse(null);
        if (usuario != null && usuario.getId() != null) {
            return usuario.getId();
        }
        return "1";
    }



	
}

