package com.TableTOP.api.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CrearPartidaDTO {

	private String nombre;
    private String juego;
    private LocalDate fechaPartida;
    private double lat;
    private double lon;
    private boolean oculto;
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getJuego() {
		return juego;
	}
	public void setJuego(String juego) {
		this.juego = juego;
	}
	public LocalDate getFechaPartida() {
		return fechaPartida;
	}
	public void setFechaPartida(LocalDate fechaPartida) {
		this.fechaPartida = fechaPartida;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public boolean isOculto() {
		return oculto;
	}
	public void setOculto(boolean oculto) {
		this.oculto = oculto;
	}
	public CrearPartidaDTO() {
		super();
	}
	public CrearPartidaDTO(String nombre, String juego, LocalDate fechaPartida, double lat, double lon,
			boolean oculto) {
		super();
		this.nombre = nombre;
		this.juego = juego;
		this.fechaPartida = fechaPartida;
		this.lat = lat;
		this.lon = lon;
		this.oculto = oculto;
	}
}
