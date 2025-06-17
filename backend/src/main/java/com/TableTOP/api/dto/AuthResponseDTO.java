package com.TableTOP.api.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String id;
    private String username;

    public AuthResponseDTO(String token, String id, String username) {
        this.token = token;
        this.id = id;
        this.username = username;
    }
    
    public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public AuthResponseDTO() {}
}
