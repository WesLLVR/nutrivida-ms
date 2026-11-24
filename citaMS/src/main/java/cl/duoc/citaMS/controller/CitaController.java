package cl.duoc.citaMS.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.citaMS.dto.SolicitudCitaDTO;
import cl.duoc.citaMS.security.Permisos;
import cl.duoc.citaMS.service.CitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/citas")
@Tag(name = "Citas", description = "Agendamiento de citas con los nutricionistas")
public class CitaController {

    @Autowired
    private CitaService service;

    @PostMapping
    @Operation(summary = "Solicitar una cita (Paciente)")
    public ResponseEntity<?> solicitar(@RequestBody SolicitudCitaDTO datos, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "PACIENTE")) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "Solo los pacientes pueden solicitar citas"));
        }
        try {
            return ResponseEntity.status(201).body(service.solicitar(Permisos.usuarioId(request), datos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Listar todas las citas (Administrador)")
    public ResponseEntity<?> listar(HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No tienes permiso para esta acción"));
        }
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/mis-citas")
    @Operation(summary = "Citas del paciente o agenda del nutricionista que inició sesión")
    public ResponseEntity<?> misCitas(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.misCitas(rol(request), Permisos.usuarioId(request), Permisos.correo(request)));
        } catch (Exception e) {
            return ResponseEntity.status(403).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/horas-ocupadas")
    @Operation(summary = "Horas ya tomadas de un nutricionista en una fecha")
    public ResponseEntity<?> horasOcupadas(@RequestParam Integer nutricionistaId, @RequestParam LocalDate fecha) {
        return ResponseEntity.ok(service.horasOcupadas(nutricionistaId, fecha));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver una cita (solo sus participantes o el administrador)")
    public ResponseEntity<?> buscar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.buscar(id, rol(request), Permisos.usuarioId(request), Permisos.correo(request)));
        } catch (Exception e) {
            return error(e);
        }
    }

    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar una cita (Nutricionista o Administrador)")
    public ResponseEntity<?> confirmar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.confirmar(id, rol(request), Permisos.correo(request)));
        } catch (Exception e) {
            return error(e);
        }
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una cita")
    public ResponseEntity<?> cancelar(@PathVariable Integer id, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.cancelar(id, rol(request), Permisos.usuarioId(request), Permisos.correo(request)));
        } catch (Exception e) {
            return error(e);
        }
    }

    @PutMapping("/{id}/reprogramar")
    @Operation(summary = "Cambiar la fecha y hora de una cita (Paciente dueño o Administrador)")
    public ResponseEntity<?> reprogramar(@PathVariable Integer id, @RequestBody SolicitudCitaDTO datos,
            HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.reprogramar(id, datos.getFecha(), datos.getHora(), rol(request),
                    Permisos.usuarioId(request)));
        } catch (Exception e) {
            return error(e);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una cita (Administrador)")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No tienes permiso para esta acción"));
        }
        try {
            service.eliminar(id);
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
