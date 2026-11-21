package cl.duoc.catalogoMS.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.catalogoMS.model.Nutricionista;
import cl.duoc.catalogoMS.security.Permisos;
import cl.duoc.catalogoMS.service.NutricionistaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/nutricionistas")
@Tag(name = "Nutricionistas", description = "Directorio de nutricionistas de la clínica")
public class NutricionistaController {

    @Autowired
    private NutricionistaService service;

    @GetMapping
    @Operation(summary = "Listar nutricionistas (público)")
    public ResponseEntity<List<Nutricionista>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar nutricionista por ID (público)")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/correo/{correo}")
    @Operation(summary = "Buscar nutricionista por correo, lo usan citaMS y fichaMS (público)")
    public ResponseEntity<?> buscarPorCorreo(@PathVariable String correo) {
        try {
            return ResponseEntity.ok(service.buscarPorCorreo(correo));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Agregar un nutricionista (Administrador)")
    public ResponseEntity<?> guardar(@RequestBody Nutricionista nutricionista, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return sinPermiso();
        }
        try {
            return ResponseEntity.status(201).body(service.guardar(nutricionista));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Datos inválidos o correo repetido"));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un nutricionista (Administrador)")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Nutricionista nutricionista,
            HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return sinPermiso();
        }
        try {
            return ResponseEntity.ok(service.actualizar(id, nutricionista));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un nutricionista (Administrador)")
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
