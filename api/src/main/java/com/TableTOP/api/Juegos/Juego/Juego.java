package com.TableTOP.api.Juegos.Juego;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "juego")
public class Juego {
	@Id
	private String id;
	private String nombre;
	private String Categoria;
	private List<String> Etiquetas;
}