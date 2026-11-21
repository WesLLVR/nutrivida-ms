package cl.duoc.usuarioMS.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.usuarioMS.dto.LoginDTO;
import cl.duoc.usuarioMS.dto.RegistroDTO;
import cl.duoc.usuarioMS.security.Permisos;
import cl.duoc.usuarioMS.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y renovación del token JWT")
public class AuthController {

    @Autowired
    private UsuarioService service;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener un token JWT")
    public ResponseEntity<?> login(@RequestBody LoginDTO datos) {
        try {
            return ResponseEntity.ok(service.login(datos.getCorreo(), datos.getClave()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar un nuevo paciente")
    public ResponseEntity<?> registrar(@RequestBody RegistroDTO datos) {
        try {
            return ResponseEntity.status(201).body(service.registrar(datos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/renovar")
    @Operation(summary = "Renovar el token JWT antes de que expire")
    public ResponseEntity<?> renovar(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.renovar(Permisos.correo(request)));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("mensaje", e.getMessage()));
        }
    }
}
