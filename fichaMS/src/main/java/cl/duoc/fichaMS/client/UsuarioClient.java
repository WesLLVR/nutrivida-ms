package cl.duoc.fichaMS.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.duoc.fichaMS.dto.UsuarioDTO;

@FeignClient(name = "usuarioMS")
public interface UsuarioClient {

    @GetMapping("/api/v1/usuarios/dto/{id}")
    UsuarioDTO obtenerUsuario(@PathVariable("id") Integer id);
}
