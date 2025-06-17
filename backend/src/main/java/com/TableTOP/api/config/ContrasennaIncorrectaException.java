package com.TableTOP.api.config;

public class ContrasennaIncorrectaException extends RuntimeException {
    public ContrasennaIncorrectaException(String mensaje) {
        super(mensaje);
    }
}
