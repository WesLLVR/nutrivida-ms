package cl.duoc.ventaMS;

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

import cl.duoc.ventaMS.client.CatalogoClient;
import cl.duoc.ventaMS.dto.CompraDTO;
import cl.duoc.ventaMS.dto.ItemDTO;
import cl.duoc.ventaMS.dto.ServicioDTO;
import cl.duoc.ventaMS.model.Venta;
import cl.duoc.ventaMS.repository.VentaRepository;
import cl.duoc.ventaMS.service.VentaService;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private VentaService ventaService;

    private CompraDTO crearCompra(String numeroTarjeta) {
        return new CompraDTO("Matías Vergara", "198765430", "usuario@correo.cl", "987654321", "Débito",
                numeroTarjeta, null, List.of(new ItemDTO("CN001", 2), new ItemDTO("TG001", 1)));
    }

    @Test
    void ocultarTarjeta_debeMostrarSoloLosUltimos4Digitos() {
        assertEquals("**** **** **** 3456", ventaService.ocultarTarjeta("4111 1111 1234 3456"));
    }

    @Test
    void comprar_debeCalcularTotalNetoEIvaConPreciosDelCatalogo() {
        when(catalogoClient.obtenerServicio("CN001")).thenReturn(new ServicioDTO(1, "CN001", "Primera consulta", 35000));
        when(catalogoClient.obtenerServicio("TG001")).thenReturn(new ServicioDTO(16, "TG001", "Taller", 15000));
        when(ventaRepository.findTopByOrderByNumeroDesc()).thenReturn(Optional.empty());
        when(ventaRepository.save(any(Venta.class))).thenAnswer(i -> i.getArgument(0));

        Venta venta = ventaService.comprar(2, crearCompra("4111111111111234"));

        assertEquals(85000, venta.getTotal());
        assertEquals(71429, venta.getNeto());
        assertEquals(13571, venta.getIva());
        assertEquals(1001, venta.getNumero());
        assertEquals("**** **** **** 1234", venta.getTarjeta());
    }

    @Test
    void comprar_conTarjetaIncompleta_debeLanzarError() {
        Exception e = assertThrows(IllegalArgumentException.class,
                () -> ventaService.comprar(2, crearCompra("4111 1111")));

        assertEquals("El número de tarjeta debe tener 16 dígitos", e.getMessage());
        verify(ventaRepository, never()).save(any());
    }

    @Test
    void comprar_conReservaVacia_debeLanzarError() {
        CompraDTO compra = crearCompra("4111111111111234");
        compra.setItems(List.of());

        assertThrows(IllegalArgumentException.class, () -> ventaService.comprar(2, compra));
    }
}
