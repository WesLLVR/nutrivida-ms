package cl.duoc.usuarioMS.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.usuarioMS.dto.UsuarioDTO;
import cl.duoc.usuarioMS.dto.UsuarioFormDTO;
import cl.duoc.usuarioMS.model.Usuario;
import cl.duoc.usuarioMS.security.Permisos;
import cl.duoc.usuarioMS.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Perfil y administración de usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping("/perfil")
    @Operation(summary = "Ver el perfil del usuario que inició sesión")
    public ResponseEntity<?> perfil(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.buscarPorId(Permisos.usuarioId(request)));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/dto/{id}")
    @Operation(summary = "Datos básicos de un usuario para otros microservicios")
    public ResponseEntity<?> obtenerDTO(@PathVariable Integer id) {
        try {
            Usuario usuario = service.buscarPorId(id);
            return ResponseEntity.ok(new UsuarioDTO(usuario.getId(), usuario.getNombre(),
                    usuario.getApellidos(), usuario.getCorreo()));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios (Administrador)")
    public ResponseEntity<?> listar(HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return sinPermiso();
        }
        List<Usuario> lista = service.listar();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por ID (Administrador)")
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

    @PostMapping
    @Operation(summary = "Crear un usuario con su rol (Administrador)")
    public ResponseEntity<?> crear(@RequestBody UsuarioFormDTO datos, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return sinPermiso();
        }
        try {
            return ResponseEntity.status(201).body(service.crear(datos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar un usuario y asignar su rol (Administrador)")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody UsuarioFormDTO datos,
            HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return sinPermiso();
        }
        try {
            return ResponseEntity.ok(service.actualizar(id, datos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Activar o desactivar un usuario (Administrador)")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return sinPermiso();
        }
        try {
            return ResponseEntity.ok(service.cambiarEstado(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario (Administrador)")
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
