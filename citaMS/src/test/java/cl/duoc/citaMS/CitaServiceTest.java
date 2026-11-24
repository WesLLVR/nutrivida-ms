package cl.duoc.citaMS;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.citaMS.client.CatalogoClient;
import cl.duoc.citaMS.client.UsuarioClient;
import cl.duoc.citaMS.dto.NutricionistaDTO;
import cl.duoc.citaMS.dto.SolicitudCitaDTO;
import cl.duoc.citaMS.model.Cita;
import cl.duoc.citaMS.repository.CitaRepository;
import cl.duoc.citaMS.service.CitaService;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private CitaService citaService;

    private final LocalDate manana = LocalDate.now().plusDays(1);

    @Test
    void solicitar_conDatosCorrectos_debeQuedarPendiente() {
        when(citaRepository.existsByNutricionistaIdAndFechaAndHoraAndEstadoNot(1, manana, "10:00", "CANCELADA"))
                .thenReturn(false);
        when(catalogoClient.obtenerNutricionista(1)).thenReturn(new NutricionistaDTO(1, "Carolina Fuentes", "Obesidad", "carolina.fuentes@nutrivida.cl"));
        when(citaRepository.save(any(Cita.class))).thenAnswer(i -> i.getArgument(0));

        Cita cita = citaService.solicitar(2, new SolicitudCitaDTO(1, manana, "10:00", "Bajar de peso"));

        assertEquals("PENDIENTE", cita.getEstado());
        assertEquals(2, cita.getPacienteId());
    }

    @Test
    void solicitar_conFechaPasada_debeLanzarError() {
        SolicitudCitaDTO datos = new SolicitudCitaDTO(1, LocalDate.now().minusDays(1), "10:00", "Control");

        Exception e = assertThrows(IllegalArgumentException.class, () -> citaService.solicitar(2, datos));

        assertEquals("La fecha no puede ser anterior a hoy", e.getMessage());
    }

    @Test
    void solicitar_conHorarioOcupado_debeLanzarError() {
        when(citaRepository.existsByNutricionistaIdAndFechaAndHoraAndEstadoNot(1, manana, "10:00", "CANCELADA"))
                .thenReturn(true);

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> citaService.solicitar(2, new SolicitudCitaDTO(1, manana, "10:00", "Control")));

        assertEquals("El nutricionista ya tiene una cita en ese horario", e.getMessage());
    }

    @Test
    void confirmar_porNutricionistaDeOtraAgenda_debeLanzarError() {
        Cita cita = new Cita(5, 2, 1, manana, "10:00", "Control", "PENDIENTE");
        when(citaRepository.findById(5)).thenReturn(Optional.of(cita));
        when(catalogoClient.obtenerNutricionistaPorCorreo("rodrigo.sepulveda@nutrivida.cl"))
                .thenReturn(new NutricionistaDTO(2, "Rodrigo Sepúlveda", "Deportiva", "rodrigo.sepulveda@nutrivida.cl"));

        assertThrows(SecurityException.class,
                () -> citaService.confirmar(5, "NUTRICIONISTA", "rodrigo.sepulveda@nutrivida.cl"));
    }

    @Test
    void cancelar_porElPacienteDueno_debeQuedarCancelada() {
        Cita cita = new Cita(5, 2, 1, manana, "10:00", "Control", "CONFIRMADA");
        when(citaRepository.findById(5)).thenReturn(Optional.of(cita));
        when(citaRepository.save(any(Cita.class))).thenAnswer(i -> i.getArgument(0));

        Cita resultado = citaService.cancelar(5, "PACIENTE", 2, "usuario@correo.cl");

        assertEquals("CANCELADA", resultado.getEstado());
    }

    @Test
    void cancelar_porOtroPaciente_debeLanzarError() {
        Cita cita = new Cita(5, 2, 1, manana, "10:00", "Control", "CONFIRMADA");
        when(citaRepository.findById(5)).thenReturn(Optional.of(cita));

        assertThrows(SecurityException.class, () -> citaService.cancelar(5, "PACIENTE", 9, "otro@correo.cl"));
    }
}
