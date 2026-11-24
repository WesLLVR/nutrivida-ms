package cl.duoc.fichaMS;

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

import cl.duoc.fichaMS.client.CatalogoClient;
import cl.duoc.fichaMS.client.UsuarioClient;
import cl.duoc.fichaMS.dto.NutricionistaDTO;
import cl.duoc.fichaMS.dto.UsuarioDTO;
import cl.duoc.fichaMS.model.Ficha;
import cl.duoc.fichaMS.repository.FichaRepository;
import cl.duoc.fichaMS.service.FichaService;

@ExtendWith(MockitoExtension.class)
class FichaServiceTest {

    @Mock
    private FichaRepository fichaRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private FichaService fichaService;

    @Test
    void calcularImc_debeRedondearAUnDecimal() {
        assertEquals(29.0, fichaService.calcularImc(92.0, 1.78));
    }

    @Test
    void crear_debeCalcularImcYAsignarAlNutricionista() {
        when(usuarioClient.obtenerUsuario(2)).thenReturn(new UsuarioDTO(2, "Matías", "Vergara", "usuario@correo.cl"));
        when(catalogoClient.obtenerNutricionistaPorCorreo("carolina.fuentes@nutrivida.cl"))
                .thenReturn(new NutricionistaDTO(1, "Carolina Fuentes", "Obesidad", "carolina.fuentes@nutrivida.cl"));
        when(fichaRepository.save(any(Ficha.class))).thenAnswer(i -> i.getArgument(0));

        Ficha ficha = new Ficha(null, 2, null, LocalDate.now(), 70.0, 1.60, 90.0, null, "Control", "Plan");
        Ficha resultado = fichaService.crear(ficha, "carolina.fuentes@nutrivida.cl");

        assertEquals(27.3, resultado.getImc());
        assertEquals(1, resultado.getNutricionistaId());
    }

    @Test
    void crear_conPesoInvalido_debeLanzarError() {
        Ficha ficha = new Ficha(null, 2, null, LocalDate.now(), 0.0, 1.60, 90.0, null, "Control", "Plan");

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> fichaService.crear(ficha, "carolina.fuentes@nutrivida.cl"));

        assertEquals("El peso debe estar entre 2 y 300 kg", e.getMessage());
    }

    @Test
    void buscar_porOtroPaciente_debeLanzarError() {
        Ficha ficha = new Ficha(1, 2, 1, LocalDate.now(), 80.0, 1.60, 90.0, 31.3, "Control", "Plan");
        when(fichaRepository.findById(1)).thenReturn(Optional.of(ficha));

        assertThrows(SecurityException.class, () -> fichaService.buscar(1, "PACIENTE", 7, "otro@correo.cl"));
    }

    @Test
    void actualizar_porNutricionistaQueNoLaCreo_debeLanzarError() {
        Ficha ficha = new Ficha(1, 2, 1, LocalDate.now(), 80.0, 1.60, 90.0, 31.3, "Control", "Plan");
        when(fichaRepository.findById(1)).thenReturn(Optional.of(ficha));
        when(catalogoClient.obtenerNutricionistaPorCorreo("felipe.araya@nutrivida.cl"))
                .thenReturn(new NutricionistaDTO(4, "Felipe Araya", "Pediátrica", "felipe.araya@nutrivida.cl"));

        assertThrows(SecurityException.class,
                () -> fichaService.actualizar(1, ficha, "felipe.araya@nutrivida.cl"));
    }
}
