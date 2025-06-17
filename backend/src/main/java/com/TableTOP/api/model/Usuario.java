package com.TableTOP.api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    private List<String> historial;

    private List<String> IDsolicitudes;

    private LocalDate fechaCreacion;

	public Usuario(String id,String username, String email, String password, LocalDate fechaCreacion) {
		super();
		this.id=id; 
		this.username = username;
		this.email = email;
		this.password = password;
		historial = new ArrayList<>();
		IDsolicitudes = new ArrayList<>();
		this.fechaCreacion = fechaCreacion;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getHistorial() {
		return historial;
	}

	public void setHistorial(List<String> historial) {
		this.historial = historial;
	}

	public List<String> getIDsolicitudes() {
		return IDsolicitudes;
	}

	public void setIDsolicitudes(List<String> iDsolicitudes) {
		IDsolicitudes = iDsolicitudes;
	}

	public LocalDate getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDate fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
}
