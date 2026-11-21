package cl.duoc.usuarioMS.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.usuarioMS.model.Comuna;
import cl.duoc.usuarioMS.model.Region;
import cl.duoc.usuarioMS.model.Rol;
import cl.duoc.usuarioMS.repository.ComunaRepository;
import cl.duoc.usuarioMS.repository.RegionRepository;
import cl.duoc.usuarioMS.repository.RolRepository;
import cl.duoc.usuarioMS.security.Permisos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Regiones y roles", description = "Datos de apoyo para los formularios")
public class RegionController {

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ComunaRepository comunaRepository;

    @Autowired
    private RolRepository rolRepository;

    @GetMapping("/regiones")
    @Operation(summary = "Listar regiones")
    public ResponseEntity<List<Region>> listarRegiones() {
        return ResponseEntity.ok(regionRepository.findAll());
    }

    @GetMapping("/regiones/{id}/comunas")
    @Operation(summary = "Listar las comunas de una región")
    public ResponseEntity<List<Comuna>> listarComunas(@PathVariable Integer id) {
        return ResponseEntity.ok(comunaRepository.findByRegionId(id));
    }

    @GetMapping("/roles")
    @Operation(summary = "Listar roles (Administrador)")
    public ResponseEntity<?> listarRoles(HttpServletRequest request) {
        if (!Permisos.tieneRol(request, "ADMINISTRADOR")) {
            return ResponseEntity.status(403).body(Map.of("mensaje", "No tienes permiso para esta acción"));
        }
        List<Rol> roles = rolRepository.findAll();
        return ResponseEntity.ok(roles);
    }
}
