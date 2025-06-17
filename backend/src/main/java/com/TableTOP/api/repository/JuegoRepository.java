package com.TableTOP.api.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

import com.TableTOP.api.model.Juego;

public interface JuegoRepository extends MongoRepository<Juego, String> {
	@NonNull
	List<Juego> findAll();
}
