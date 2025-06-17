package com.TableTOP.api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "partidas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Partida {
    @Id
    private String id;
    private String nombre;
    private String juego;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fechaPartida;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private boolean oculto;
    
    @JsonProperty("IDcreador")
    private String IDcreador;

    @Builder.Default
    private List<String> participantes = new ArrayList<>();

    @Builder.Default
    private List<String> solicitantes = new ArrayList<>();

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

	public GeoJsonPoint getLocation() {
		return location;
	}

	public void setLocation(GeoJsonPoint location) {
		this.location = location;
	}

	public boolean isOculto() {
		return oculto;
	}

	public void setOculto(boolean oculto) {
		this.oculto = oculto;
	}

	public String getIDcreador() {
		return IDcreador;
	}

	public void setIDcreador(String iDcreador) {
		IDcreador = iDcreador;
	}

	public List<String> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(List<String> participantes) {
		this.participantes = participantes;
	}

	public List<String> getSolicitantes() {
		return solicitantes;
	}

	public void setSolicitantes(List<String> solicitantes) {
		this.solicitantes = solicitantes;
	}
    
}
