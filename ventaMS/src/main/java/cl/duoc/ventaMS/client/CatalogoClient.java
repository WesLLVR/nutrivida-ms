package cl.duoc.ventaMS.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.duoc.ventaMS.dto.ServicioDTO;

@FeignClient(name = "catalogoMS")
public interface CatalogoClient {

    @GetMapping("/api/v1/servicios/codigo/{codigo}")
    ServicioDTO obtenerServicio(@PathVariable("codigo") String codigo);
}
