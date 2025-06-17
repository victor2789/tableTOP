package com.TableTOP.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TableTOP.api.model.Juego;
import com.TableTOP.api.repository.JuegoRepository;

@Service
public class JuegoService {
	@Autowired
	private JuegoRepository juegoRepository;

	public List<Juego> insertarJuegos(List<Juego> juegos) {
	    return juegoRepository.saveAll(juegos); 
	}

	public List<Juego> obtenerTodosLosJuegos() {
	    return juegoRepository.findAll();
	}


}
