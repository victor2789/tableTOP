package com.TableTOP.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.TableTOP.api.model.Juego;
import com.TableTOP.api.service.JuegoService;

import java.util.List;

@RestController
@RequestMapping("/api/juegos")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class JuegoController {

	@Autowired
	private JuegoService juegoService;

	@PostMapping("/insertar-muchos")
	public List<Juego> insertarMuchos(@RequestBody List<Juego> juegos) {
	    return juegoService.insertarJuegos(juegos);
	}

	@GetMapping
	public List<Juego> listarTodos() {
	    return juegoService.obtenerTodosLosJuegos();
	}
}
