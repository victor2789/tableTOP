package com.TableTOP.api.controller;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TableTOP.api.dto.CrearPartidaDTO;
import com.TableTOP.api.model.Partida;
import com.TableTOP.api.model.Usuario;
import com.TableTOP.api.repository.PartidaRepository;
import com.TableTOP.api.service.JwtService;
import com.TableTOP.api.service.PartidaService;
import com.TableTOP.api.service.UsuarioService;

@RestController
@RequestMapping("/api/partidas")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS })
public class PartidaController {

    private final PartidaService partidaService;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final PartidaRepository partidaRepository;

    public PartidaController(PartidaService partidaService, UsuarioService usuarioService, JwtService jwtService,
            PartidaRepository partidaRepository) {
        this.partidaService = partidaService;
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.partidaRepository = partidaRepository;
    }

    private String getUserId(HttpServletRequest request) {
        String token = jwtService.extractToken(request);
        String username = jwtService.extractUsername(token);
        return usuarioService.getUsuarioByUsername(username).map(Usuario::getId).orElse(null);
    }

    private static final int MAX_PARTICIPANTES = 4;

    @PostMapping("/{id}/solicitar")
    public ResponseEntity<?> solicitarUnirse(@PathVariable String id, HttpServletRequest request) {
        String userId = getUserId(request);
        Optional<Partida> partidaOpt = partidaService.getById(id);

        if (partidaOpt.isPresent() && userId != null) {
            Partida partida = partidaOpt.get();

            if (partida.getParticipantes().size() >= MAX_PARTICIPANTES) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("La partida ya está completa");
            }

            if (partida.getParticipantes().contains(userId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya eres participante");
            }

            if (partida.getSolicitantes().contains(userId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Solicitud ya enviada");
            }

            partida.getSolicitantes().add(userId);
            partidaService.guardarPartida(partida);
            return ResponseEntity.ok("Solicitud enviada");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Partida no encontrada");
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearPartida(@RequestBody CrearPartidaDTO dto, HttpServletRequest request) {
        String userId = getUserId(request);
        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");

        try {
            Partida nueva = Partida.builder()
                    .nombre(dto.getNombre())
                    .juego(dto.getJuego())
                    .fechaPartida(dto.getFechaPartida())
                    .location(new GeoJsonPoint(dto.getLon(), dto.getLat()))
                    .oculto(dto.isOculto())
                    .IDcreador(userId)
                    .participantes(new ArrayList<>())
                    .solicitantes(new ArrayList<>())
                    .build();

            partidaService.guardarPartida(nueva);
            return ResponseEntity.ok("Partida creada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear partida: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/aceptar/{userId}")
    public ResponseEntity<?> aceptarSolicitud(@PathVariable String id, @PathVariable String userId,
            HttpServletRequest request) {
        String currentUserId = getUserId(request);
        Optional<Partida> partidaOpt = partidaService.getById(id);

        if (partidaOpt.isPresent()) {
            Partida partida = partidaOpt.get();
            if (!partida.getIDcreador().equals(currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No eres el creador de esta partida");
            }
            if (partida.getParticipantes().size() >= MAX_PARTICIPANTES) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("La partida ya está completa");
            }
            if (partida.getSolicitantes().remove(userId)) {
                partida.getParticipantes().add(userId);
                partidaService.guardarPartida(partida);
                usuarioService.agregarAPartida(userId, id);
                return ResponseEntity.ok("Usuario aceptado");
            }
            return ResponseEntity.badRequest().body("El usuario no estaba en la lista de solicitantes");
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/rechazar/{userId}")
    public ResponseEntity<?> rechazarSolicitud(@PathVariable String id, @PathVariable String userId,
            HttpServletRequest request) {
        String currentUserId = getUserId(request);
        Optional<Partida> partidaOpt = partidaService.getById(id);

        if (partidaOpt.isPresent()) {
            Partida partida = partidaOpt.get();
            if (!partida.getIDcreador().equals(currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No eres el creador de esta partida");
            }
            if (partida.getSolicitantes().remove(userId)) {
                partidaService.guardarPartida(partida);
                return ResponseEntity.ok("Solicitud rechazada");
            }
            return ResponseEntity.badRequest().body("El usuario no estaba en la lista de solicitantes");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/participantes")
    public ResponseEntity<List<String>> verParticipantes(@PathVariable String id) {
        return partidaService.getById(id)
                .map(p -> ResponseEntity.ok(p.getParticipantes()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Partida>> buscarPartidas(@RequestParam double lat, @RequestParam double lon,
            @RequestParam(defaultValue = "10000") int distancia, @RequestParam(defaultValue = "") String juego,
            @RequestParam(defaultValue = "false") boolean completas, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                partidaService.buscarPartidas(lat, lon, distancia, juego, completas, page, size));
    }

    // Fuerza a usar IDcreador en lugar de cambiarlo a idCreador al pasar a json

    @GetMapping("/{id}")
    public ResponseEntity<?> getPartida(@PathVariable String id) {
        return partidaService.getById(id)
                .map(p -> {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("id", p.getId());
                    datos.put("nombre", p.getNombre());
                    datos.put("juego", p.getJuego());
                    datos.put("fechaPartida", p.getFechaPartida());
                    datos.put("location", p.getLocation());
                    datos.put("oculto", p.isOculto());
                    datos.put("participantes", p.getParticipantes());
                    datos.put("solicitantes", p.getSolicitantes());
                    datos.put("IDcreador", p.getIDcreador());             // clave exactamente en mayúsculas
                    return ResponseEntity.ok(datos);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/solicitantes")
    public ResponseEntity<?> verSolicitantes1(@PathVariable String id, HttpServletRequest request) {
        String currentUserId = getUserId(request);
        return partidaService.getById(id)
                .filter(p -> p.getIDcreador().equals(currentUserId))
                .map(p -> {
                    List<String> usernames = p.getSolicitantes().stream()
                            .map(userId -> usuarioService.getUsuarioById(userId)
                                    .map(Usuario::getUsername)
                                    .orElse("Desconocido"))
                            .collect(Collectors.toList());
                    return ResponseEntity.ok(usernames);
                })
                .orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @GetMapping("/yo")
    public ResponseEntity<?> obtenerUsuarioActual(HttpServletRequest request) {
        String username = jwtService.extractUsername(jwtService.extractToken(request));
        return usuarioService.getUsuarioByUsername(username)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado"));
    }

    @PostMapping("/usernames")
    public ResponseEntity<Map<String, String>> getUsernamesFromIds(@RequestBody List<String> ids) {
        System.out.println("IDs recibidos en /usernames: " + ids);
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of());
        }
        Map<String, String> map = ids.stream()
                .filter(Objects::nonNull)
                .map(id -> {
                    try {
                        return usuarioService.getUsuarioById(id)
                                .map(u -> Map.entry(id, u.getUsername()))
                                .orElse(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return ResponseEntity.ok(map);
    }
}
