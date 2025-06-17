package com.TableTOP.api.config;

import java.util.List;

public class SecurityConstants {
    public static final List<String> PUBLIC_ENDPOINTS = List.of(
        "/auth/register",
        "/auth/login",
        "/api/juegos",
        "/api/partidas/crear",
        "/api/partidas/buscar"      
    );

    public static boolean isPublic(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }
}
