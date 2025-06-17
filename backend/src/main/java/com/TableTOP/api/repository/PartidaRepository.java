package com.TableTOP.api.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.TableTOP.api.model.Partida;

import java.util.List;

public interface PartidaRepository extends MongoRepository<Partida, String> {
    
    List<Partida> findByIDcreador(String idCreador);
    
    List<Partida> findByParticipantesContaining(String idUsuario);

    @Query("""
        {
            'location': {
                $near: {
                    $geometry: { type: 'Point', coordinates: [?0, ?1] },
                    $maxDistance: ?2
                }
            },
            'juego': { $regex: ?3, $options: 'i' },
            $expr: {
                $or: [
                    { $lt: [{ $size: "$participantes" }, 4] },
                    { $cond: { if: ?4, then: true, else: false } }
                ]
            }
        }
    """)
    List<Partida> buscarPartidasCercanas(double lon, double lat, double maxDist, String juegoNombre, boolean incluirCompletas, Pageable pageable);
}
