package cl.duoc.ventaMS.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.ventaMS.dto.CompraDTO;
import cl.duoc.ventaMS.security.Permisos;
import cl.duoc.ventaMS.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/ventas")
@Tag(name = "Ventas", description = "Compra simulada de servicios y boletas")
public class VentaController {

    @Autowired
    private VentaService service;

    @PostMapping
    @Operation(summary = "Pagar la reserva y generar la boleta (Paciente)")
    public ResponseEntity<?> comprar(@RequestBody CompraDTO datos, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "PACIENTE")) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "Solo los pacientes pueden comprar servicios"));
        }
        try {
            return ResponseEntity.status(201).body(service.comprar(Permisos.usuarioId(request), datos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Listar todas las ventas (Administrador)")
    public ResponseEntity<?> listar(HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No tienes permiso para esta acción"));
        }
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/mis-compras")
    @Operation(summary = "Boletas del paciente que inició sesión")
    public ResponseEntity<?> misCompras(HttpServletRequest request) {
        return ResponseEntity.ok(service.misCompras(Permisos.usuarioId(request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver una boleta (dueño o Administrador)")
    public ResponseEntity<?> buscar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.buscar(id, (String) request.getAttribute("rol"), Permisos.usuarioId(request)));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}/anular")
    @Operation(summary = "Anular una venta (Administrador)")
    public ResponseEntity<?> anular(@PathVariable Integer id, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No tienes permiso para esta acción"));
        }
        try {
            return ResponseEntity.ok(service.anular(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una venta (Administrador)")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No tienes permiso para esta acción"));
        }
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }
}
