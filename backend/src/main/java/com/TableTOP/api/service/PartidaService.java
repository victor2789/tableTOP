package com.TableTOP.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.TableTOP.api.model.Partida;
import com.TableTOP.api.repository.PartidaRepository;

@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;

    public PartidaService(PartidaRepository partidaRepository) {
        this.partidaRepository = partidaRepository;
    }

    public Optional<Partida> getById(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        return partidaRepository.findById(id);
    }

    public Partida guardarPartida(Partida partida) {
        if (partida == null) throw new IllegalArgumentException("La partida no puede ser nula");
        return partidaRepository.save(partida);
    }

    public List<Partida> buscarPartidas(double lat, double lon, int distanciaKm, String juego, boolean incluirCompletas, int page, int size) {
        double distanciaMetros = distanciaKm * 1000;
        Pageable pageable = PageRequest.of(page, size);
        return partidaRepository.buscarPartidasCercanas(lon, lat, distanciaMetros, juego, incluirCompletas, pageable);
    }
}
