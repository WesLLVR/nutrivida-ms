package cl.duoc.catalogoMS.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.catalogoMS.model.Nutricionista;
import cl.duoc.catalogoMS.repository.NutricionistaRepository;

@Service
public class NutricionistaService {

    @Autowired
    private NutricionistaRepository repository;

    public List<Nutricionista> listar() {
        return repository.findAll();
    }

    public Nutricionista buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nutricionista no encontrado"));
    }

    public Nutricionista buscarPorCorreo(String correo) {
        return repository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Nutricionista no encontrado"));
    }

    public Nutricionista guardar(Nutricionista nutricionista) {
        nutricionista.setId(null);
        return repository.save(nutricionista);
    }

    public Nutricionista actualizar(Integer id, Nutricionista datos) {
        Nutricionista nutricionista = buscarPorId(id);
        nutricionista.setCodigo(datos.getCodigo());
        nutricionista.setNombre(datos.getNombre());
        nutricionista.setEspecialidad(datos.getEspecialidad());
        nutricionista.setDiasAtencion(datos.getDiasAtencion());
        nutricionista.setHorario(datos.getHorario());
        nutricionista.setCorreo(datos.getCorreo());
        return repository.save(nutricionista);
    }

    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Nutricionista no encontrado");
        }
        repository.deleteById(id);
    }
}
