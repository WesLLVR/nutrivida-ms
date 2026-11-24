package cl.duoc.citaMS.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.duoc.citaMS.dto.NutricionistaDTO;

@FeignClient(name = "catalogoMS")
public interface CatalogoClient {

    @GetMapping("/api/v1/nutricionistas/{id}")
    NutricionistaDTO obtenerNutricionista(@PathVariable("id") Integer id);

    @GetMapping("/api/v1/nutricionistas/correo/{correo}")
    NutricionistaDTO obtenerNutricionistaPorCorreo(@PathVariable("correo") String correo);
}
