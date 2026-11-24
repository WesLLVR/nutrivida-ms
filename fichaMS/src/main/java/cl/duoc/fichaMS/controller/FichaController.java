package cl.duoc.fichaMS.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.fichaMS.model.Ficha;
import cl.duoc.fichaMS.security.Permisos;
import cl.duoc.fichaMS.service.FichaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/fichas")
@Tag(name = "Fichas", description = "Mediciones y plan alimenticio de cada paciente")
public class FichaController {

    @Autowired
    private FichaService service;

    @PostMapping
    @Operation(summary = "Registrar una medición y plan alimenticio (Nutricionista)")
    public ResponseEntity<?> crear(@RequestBody Ficha ficha, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "NUTRICIONISTA")) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "Solo los nutricionistas registran fichas"));
        }
        try {
            return ResponseEntity.status(201).body(service.crear(ficha, Permisos.correo(request)));
        } catch (Exception e) {
            return error(e);
        }
    }

    @GetMapping
    @Operation(summary = "Listar todas las fichas (Administrador)")
    public ResponseEntity<?> listar(HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No tienes permiso para esta acción"));
        }
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/mis-fichas")
    @Operation(summary = "Historial del paciente o fichas registradas por el nutricionista")
    public ResponseEntity<?> misFichas(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.misFichas(rol(request), Permisos.usuarioId(request), Permisos.correo(request)));
        } catch (Exception e) {
            return error(e);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver una ficha (paciente dueño, su nutricionista o administrador)")
    public ResponseEntity<?> buscar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.buscar(id, rol(request), Permisos.usuarioId(request), Permisos.correo(request)));
        } catch (Exception e) {
            return error(e);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar una ficha (Nutricionista que la creó)")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Ficha ficha, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "NUTRICIONISTA")) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "Solo los nutricionistas editan fichas"));
        }
        try {
            return ResponseEntity.ok(service.actualizar(id, ficha, Permisos.correo(request)));
        } catch (Exception e) {
            return error(e);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una ficha (Nutricionista que la creó o Administrador)")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "NUTRICIONISTA", "ADMINISTRADOR")) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No tienes permiso para esta acción"));
        }
        try {
            service.eliminar(id, rol(request), Permisos.correo(request));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return error(e);
        }
    }

    private String rol(HttpServletRequest request) {
        return (String) request.getAttribute("rol");
    }

    private ResponseEntity<?> error(Exception e) {
        if (e instanceof SecurityException) {
            return ResponseEntity.status(403).body(Map.of("mensaje", e.getMessage()));
        }
        if (e instanceof IllegalArgumentException) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
        return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
    }
}
