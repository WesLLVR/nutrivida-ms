package cl.duoc.catalogoMS;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.catalogoMS.model.Servicio;
import cl.duoc.catalogoMS.model.TipoServicio;
import cl.duoc.catalogoMS.repository.ServicioRepository;
import cl.duoc.catalogoMS.repository.TipoServicioRepository;
import cl.duoc.catalogoMS.service.ServicioService;

@ExtendWith(MockitoExtension.class)
class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @Mock
    private TipoServicioRepository tipoRepository;

    @InjectMocks
    private ServicioService servicioService;

    private Servicio crearServicio(Integer precio) {
        return new Servicio(null, "TG004", "Taller de colaciones", "60 min", "Grupal", precio,
                "Ideas de colaciones saludables", "/img/servicio-taller.png", false, new TipoServicio(4, "Taller"));
    }

    @Test
    void listar_debeRetornarTodosLosServicios() {
        when(servicioRepository.findAll()).thenReturn(List.of(crearServicio(15000), crearServicio(18000)));

        assertEquals(2, servicioService.listar().size());
    }

    @Test
    void guardar_conDatosCorrectos_debeGuardarElServicio() {
        when(servicioRepository.existsByCodigo("TG004")).thenReturn(false);
        when(tipoRepository.findById(4)).thenReturn(Optional.of(new TipoServicio(4, "Taller")));
        when(servicioRepository.save(any(Servicio.class))).thenAnswer(i -> i.getArgument(0));

        Servicio guardado = servicioService.guardar(crearServicio(12000));

        assertEquals("TG004", guardado.getCodigo());
        verify(servicioRepository, times(1)).save(any(Servicio.class));
    }

    @Test
    void guardar_conPrecioCero_debeLanzarError() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> servicioService.guardar(crearServicio(0)));

        assertEquals("El precio debe ser mayor a 0", e.getMessage());
        verify(servicioRepository, never()).save(any());
    }

    @Test
    void guardar_conCodigoRepetido_debeLanzarError() {
        when(servicioRepository.existsByCodigo("TG004")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> servicioService.guardar(crearServicio(12000)));
    }

    @Test
    void buscarPorCodigo_cuandoNoExiste_debeLanzarError() {
        when(servicioRepository.findByCodigo("XX999")).thenReturn(Optional.empty());

        Exception e = assertThrows(RuntimeException.class, () -> servicioService.buscarPorCodigo("XX999"));

        assertEquals("Servicio no encontrado", e.getMessage());
    }
}
