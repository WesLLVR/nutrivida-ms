package cl.duoc.contactoMS;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.contactoMS.model.Mensaje;
import cl.duoc.contactoMS.repository.MensajeRepository;
import cl.duoc.contactoMS.service.MensajeService;

@ExtendWith(MockitoExtension.class)
class MensajeServiceTest {

    @Mock
    private MensajeRepository repository;

    @InjectMocks
    private MensajeService service;

    @Test
    void enviar_conDatosCorrectos_debeQuedarComoNoLeido() {
        when(repository.save(any(Mensaje.class))).thenAnswer(i -> i.getArgument(0));

        Mensaje guardado = service.enviar(new Mensaje(null, "Lucas", "lucas@correo.cl", "Quiero agendar", null, null));

        assertFalse(guardado.getLeido());
        assertNotNull(guardado.getFecha());
    }

    @Test
    void enviar_conCorreoInvalido_debeLanzarError() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> service.enviar(new Mensaje(null, "Lucas", "lucas.correo.cl", "Hola", null, null)));

        assertEquals("Ingrese un correo válido", e.getMessage());
    }

    @Test
    void enviar_conMensajeMuyLargo_debeLanzarError() {
        String largo = "a".repeat(501);

        assertThrows(IllegalArgumentException.class,
                () -> service.enviar(new Mensaje(null, "Lucas", "lucas@correo.cl", largo, null, null)));
    }

    @Test
    void marcarLeido_debeCambiarElEstado() {
        Mensaje mensaje = new Mensaje(1, "Lucas", "lucas@correo.cl", "Hola", null, false);
        when(repository.findById(1)).thenReturn(Optional.of(mensaje));
        when(repository.save(any(Mensaje.class))).thenAnswer(i -> i.getArgument(0));

        assertTrue(service.marcarLeido(1).getLeido());
    }
}
