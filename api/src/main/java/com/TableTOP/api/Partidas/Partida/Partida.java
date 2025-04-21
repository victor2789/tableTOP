package com.TableTOP.api.Partidas.Partida;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.TableTOP.api.Juegos.Juego.Juego;

import lombok.Data;

@Data
@Document(collection = "partida")
public class Partida {
	@Id
	private String id;
	private String nombre;
	private Juego juego;
	private LocalDate fechaCreacion;
	private LocalDate fechaPartida;
}
