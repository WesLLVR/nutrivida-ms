package cl.duoc.catalogoMS.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.catalogoMS.model.Servicio;
import cl.duoc.catalogoMS.model.TipoServicio;
import cl.duoc.catalogoMS.security.Permisos;
import cl.duoc.catalogoMS.service.ServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Servicios", description = "Catálogo de servicios y planes de la clínica")
public class ServicioController {

    @Autowired
    private ServicioService service;

    @GetMapping("/servicios")
    @Operation(summary = "Listar todos los servicios (público)")
    public ResponseEntity<List<Servicio>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/servicios/{id}")
    @Operation(summary = "Buscar servicio por ID (público)")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/servicios/codigo/{codigo}")
    @Operation(summary = "Buscar servicio por código, por ejemplo CN001 (público)")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
        try {
            return ResponseEntity.ok(service.buscarPorCodigo(codigo));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/tipos-servicio")
    @Operation(summary = "Listar los tipos de servicio (público)")
    public ResponseEntity<List<TipoServicio>> listarTipos() {
        return ResponseEntity.ok(service.listarTipos());
    }

    @PostMapping("/servicios")
    @Operation(summary = "Crear un servicio (Administrador)")
    public ResponseEntity<?> guardar(@RequestBody Servicio servicio, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return sinPermiso();
        }
        try {
            return ResponseEntity.status(201).body(service.guardar(servicio));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/servicios/{id}")
    @Operation(summary = "Actualizar un servicio (Administrador)")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Servicio servicio,
            HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return sinPermiso();
        }
        try {
            return ResponseEntity.ok(service.actualizar(id, servicio));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/servicios/{id}")
    @Operation(summary = "Eliminar un servicio (Administrador)")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return sinPermiso();
        }
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    private ResponseEntity<?> sinPermiso() {
        return ResponseEntity.status(403).body(Map.of("mensaje", "No tienes permiso para esta acción"));
    }
}
