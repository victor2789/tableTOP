package com.TableTOP.api.Partidas.Partida;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.TableTOP.api.Juegos.Juego.Juego;

import lombok.Data;

@Data
@Document(collection = "partidas")
public class Partida {
	@Id
	private String id;
	private String nombre;
	private Juego juego;
	private LocalDate fechaCreacion;
	private LocalDate fechaPartida;
	private boolean oculto;
	private int IDcreador;
	private List<String> participantes;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Juego getJuego() {
		return juego;
	}
	public void setJuego(Juego juego) {
		this.juego = juego;
	}
	public LocalDate getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(LocalDate fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	public LocalDate getFechaPartida() {
		return fechaPartida;
	}
	public void setFechaPartida(LocalDate fechaPartida) {
		this.fechaPartida = fechaPartida;
	}
	public boolean isOculto() {
		return oculto;
	}
	public void setOculto(boolean oculto) {
		this.oculto = oculto;
	}
	public int getIDcreador() {
		return IDcreador;
	}
	public void setIDcreador(int iDcreador) {
		IDcreador = iDcreador;
	}
	public List<String> getParticipantes() {
		return participantes;
	}
	public void setParticipantes(List<String> participantes) {
		this.participantes = participantes;
	}
}
