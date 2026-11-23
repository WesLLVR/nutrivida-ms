package cl.duoc.contactoMS.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.contactoMS.model.Mensaje;
import cl.duoc.contactoMS.repository.MensajeRepository;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository repository;

    public Mensaje enviar(Mensaje mensaje) {
        if (mensaje.getNombre() == null || mensaje.getNombre().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar su nombre");
        }
        if (mensaje.getCorreo() == null || !mensaje.getCorreo().matches("^[^@\\s]+@[^@\\s]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Ingrese un correo válido");
        }
        if (mensaje.getMensaje() == null || mensaje.getMensaje().isBlank()) {
            throw new IllegalArgumentException("Debe escribir un mensaje");
        }
        if (mensaje.getMensaje().length() > 500) {
            throw new IllegalArgumentException("El mensaje no puede superar los 500 caracteres");
        }

        mensaje.setId(null);
        mensaje.setFecha(LocalDateTime.now());
        mensaje.setLeido(false);
        return repository.save(mensaje);
    }

    public List<Mensaje> listar() {
        return repository.findAllByOrderByFechaDesc();
    }

    public Mensaje buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
    }

    public Mensaje marcarLeido(Integer id) {
        Mensaje mensaje = buscarPorId(id);
        mensaje.setLeido(!mensaje.getLeido());
        return repository.save(mensaje);
    }

    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Mensaje no encontrado");
        }
        repository.deleteById(id);
    }
}
