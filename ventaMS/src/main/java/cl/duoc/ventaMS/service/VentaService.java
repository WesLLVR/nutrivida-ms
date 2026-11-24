package cl.duoc.ventaMS.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.ventaMS.client.CatalogoClient;
import cl.duoc.ventaMS.dto.CompraDTO;
import cl.duoc.ventaMS.dto.ItemDTO;
import cl.duoc.ventaMS.dto.ServicioDTO;
import cl.duoc.ventaMS.model.DetalleVenta;
import cl.duoc.ventaMS.model.Venta;
import cl.duoc.ventaMS.repository.VentaRepository;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private CatalogoClient catalogoClient;

    public String ocultarTarjeta(String numero) {
        String digitos = numero.replace(" ", "");
        return "**** **** **** " + digitos.substring(digitos.length() - 4);
    }

    public int calcularNeto(int total) {
        return (int) Math.round(total / 1.19);
    }

    public Venta comprar(Integer pacienteId, CompraDTO datos) {
        validar(datos);

        Venta venta = new Venta();
        venta.setPacienteId(pacienteId);
        venta.setFecha(LocalDate.now());
        venta.setCliente(datos.getNombre());
        venta.setRun(datos.getRun());
        venta.setCorreo(datos.getCorreo());
        venta.setTelefono(datos.getTelefono());
        venta.setTipoTarjeta(datos.getTipoTarjeta());
        venta.setTarjeta(ocultarTarjeta(datos.getNumeroTarjeta()));
        venta.setCuotas(datos.getTipoTarjeta().equals("Crédito") ? datos.getCuotas() : 1);
        venta.setEstado("PAGADA");

        int total = 0;
        for (ItemDTO item : datos.getItems()) {
            ServicioDTO servicio;
            try {
                servicio = catalogoClient.obtenerServicio(item.getCodigo());
            } catch (Exception e) {
                throw new IllegalArgumentException("El servicio " + item.getCodigo() + " no existe");
            }

            int subtotal = servicio.getPrecio() * item.getCantidad();
            total = total + subtotal;
            venta.getDetalle().add(new DetalleVenta(null, venta, servicio.getCodigo(), servicio.getNombre(),
                    servicio.getPrecio(), item.getCantidad(), subtotal));
        }

        int neto = calcularNeto(total);
        venta.setTotal(total);
        venta.setNeto(neto);
        venta.setIva(total - neto);
        venta.setNumero((int) ventaRepository.count() + 1001);

        return ventaRepository.save(venta);
    }

    public List<Venta> listar() {
        return ventaRepository.findAll();
    }

    public List<Venta> misCompras(Integer pacienteId) {
        return ventaRepository.findByPacienteIdOrderByNumeroDesc(pacienteId);
    }

    public Venta buscar(Integer id, String rol, Integer usuarioId) {
        Venta venta = buscarPorId(id);
        if (!rol.equals("ADMINISTRADOR") && !venta.getPacienteId().equals(usuarioId)) {
            throw new SecurityException("No puedes ver boletas de otros pacientes");
        }
        return venta;
    }

    public Venta anular(Integer id) {
        Venta venta = buscarPorId(id);
        if (venta.getEstado().equals("ANULADA")) {
            throw new IllegalArgumentException("La venta ya está anulada");
        }
        venta.setEstado("ANULADA");
        return ventaRepository.save(venta);
    }

    public void eliminar(Integer id) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada");
        }
        ventaRepository.deleteById(id);
    }

    private Venta buscarPorId(Integer id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    private void validar(CompraDTO datos) {
        if (datos.getItems() == null || datos.getItems().isEmpty()) {
            throw new IllegalArgumentException("La reserva está vacía");
        }
        for (ItemDTO item : datos.getItems()) {
            if (item.getCantidad() == null || item.getCantidad() < 1) {
                throw new IllegalArgumentException("La cantidad debe ser al menos 1");
            }
        }
        if (datos.getNombre() == null || datos.getNombre().isBlank()
                || datos.getRun() == null || datos.getCorreo() == null) {
            throw new IllegalArgumentException("Faltan datos del cliente");
        }
        if (datos.getNumeroTarjeta() == null || !datos.getNumeroTarjeta().replace(" ", "").matches("^[0-9]{16}$")) {
            throw new IllegalArgumentException("El número de tarjeta debe tener 16 dígitos");
        }
        if (!"Débito".equals(datos.getTipoTarjeta()) && !"Crédito".equals(datos.getTipoTarjeta())) {
            throw new IllegalArgumentException("El tipo de tarjeta debe ser Débito o Crédito");
        }
        if ("Crédito".equals(datos.getTipoTarjeta())
                && (datos.getCuotas() == null || datos.getCuotas() < 1 || datos.getCuotas() > 12)) {
            throw new IllegalArgumentException("Las cuotas deben estar entre 1 y 12");
        }
    }
}
