package cl.duoc.contactoMS.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.contactoMS.model.Mensaje;
import cl.duoc.contactoMS.security.Permisos;
import cl.duoc.contactoMS.service.MensajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/contactos")
@Tag(name = "Contacto", description = "Mensajes del formulario de contacto")
public class MensajeController {

    @Autowired
    private MensajeService service;

    @PostMapping
    @Operation(summary = "Enviar un mensaje de contacto (público)")
    public ResponseEntity<?> enviar(@RequestBody Mensaje mensaje) {
        try {
            return ResponseEntity.status(201).body(service.enviar(mensaje));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Listar mensajes recibidos (Administrador)")
    public ResponseEntity<?> listar(HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return sinPermiso();
        }
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver un mensaje (Administrador)")
    public ResponseEntity<?> buscar(@PathVariable Integer id, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return sinPermiso();
        }
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}/leido")
    @Operation(summary = "Marcar o desmarcar un mensaje como leído (Administrador)")
    public ResponseEntity<?> marcarLeido(@PathVariable Integer id, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return sinPermiso();
        }
        try {
            return ResponseEntity.ok(service.marcarLeido(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un mensaje (Administrador)")
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
