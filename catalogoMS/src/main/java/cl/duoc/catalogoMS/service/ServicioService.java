package cl.duoc.catalogoMS.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.catalogoMS.model.Servicio;
import cl.duoc.catalogoMS.model.TipoServicio;
import cl.duoc.catalogoMS.repository.ServicioRepository;
import cl.duoc.catalogoMS.repository.TipoServicioRepository;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private TipoServicioRepository tipoRepository;

    public List<Servicio> listar() {
        return servicioRepository.findAll();
    }

    public List<TipoServicio> listarTipos() {
        return tipoRepository.findAll();
    }

    public Servicio buscarPorId(Integer id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
    }

    public Servicio buscarPorCodigo(String codigo) {
        return servicioRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
    }

    public Servicio guardar(Servicio servicio) {
        validar(servicio);
        if (servicioRepository.existsByCodigo(servicio.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un servicio con ese código");
        }
        servicio.setId(null);
        servicio.setTipo(buscarTipo(servicio.getTipo()));
        if (servicio.getDestacado() == null) {
            servicio.setDestacado(false);
        }
        return servicioRepository.save(servicio);
    }

    public Servicio actualizar(Integer id, Servicio datos) {
        Servicio servicio = buscarPorId(id);
        validar(datos);

        servicio.setNombre(datos.getNombre());
        servicio.setDuracion(datos.getDuracion());
        servicio.setModalidad(datos.getModalidad());
        servicio.setPrecio(datos.getPrecio());
        servicio.setDescripcion(datos.getDescripcion());
        servicio.setImagen(datos.getImagen());
        servicio.setTipo(buscarTipo(datos.getTipo()));
        if (datos.getDestacado() != null) {
            servicio.setDestacado(datos.getDestacado());
        }

        return servicioRepository.save(servicio);
    }

    public void eliminar(Integer id) {
        if (!servicioRepository.existsById(id)) {
            throw new RuntimeException("Servicio no encontrado");
        }
        servicioRepository.deleteById(id);
    }

    private void validar(Servicio servicio) {
        if (servicio.getCodigo() == null || servicio.getCodigo().isBlank()
                || servicio.getNombre() == null || servicio.getNombre().isBlank()) {
            throw new IllegalArgumentException("El código y el nombre son obligatorios");
        }
        if (servicio.getPrecio() == null || servicio.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (servicio.getTipo() == null) {
            throw new IllegalArgumentException("Debe indicar el tipo de servicio");
        }
    }

    private TipoServicio buscarTipo(TipoServicio tipo) {
        if (tipo.getId() != null) {
            return tipoRepository.findById(tipo.getId())
                    .orElseThrow(() -> new IllegalArgumentException("El tipo de servicio no existe"));
        }
        return tipoRepository.findByNombre(tipo.getNombre())
                .orElseThrow(() -> new IllegalArgumentException("El tipo de servicio no existe"));
    }
}
