package cl.duoc.citaMS.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.citaMS.client.CatalogoClient;
import cl.duoc.citaMS.client.UsuarioClient;
import cl.duoc.citaMS.dto.CitaDetalleDTO;
import cl.duoc.citaMS.dto.NutricionistaDTO;
import cl.duoc.citaMS.dto.SolicitudCitaDTO;
import cl.duoc.citaMS.dto.UsuarioDTO;
import cl.duoc.citaMS.model.Cita;
import cl.duoc.citaMS.repository.CitaRepository;

@Service
public class CitaService {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String CONFIRMADA = "CONFIRMADA";
    public static final String CANCELADA = "CANCELADA";

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private CatalogoClient catalogoClient;

    @Autowired
    private UsuarioClient usuarioClient;

    public Cita solicitar(Integer pacienteId, SolicitudCitaDTO datos) {
        if (datos.getNutricionistaId() == null || datos.getMotivo() == null || datos.getMotivo().isBlank()) {
            throw new IllegalArgumentException("Debe elegir un nutricionista e indicar el motivo");
        }
        validarHorario(datos.getNutricionistaId(), datos.getFecha(), datos.getHora());

        try {
            catalogoClient.obtenerNutricionista(datos.getNutricionistaId());
        } catch (Exception e) {
            throw new IllegalArgumentException("El nutricionista no existe");
        }

        Cita cita = new Cita(null, pacienteId, datos.getNutricionistaId(), datos.getFecha(),
                datos.getHora(), datos.getMotivo(), PENDIENTE);
        return citaRepository.save(cita);
    }

    public List<CitaDetalleDTO> listarTodas() {
        return aDetalle(citaRepository.findAll());
    }

    public List<CitaDetalleDTO> misCitas(String rol, Integer usuarioId, String correo) {
        if (rol.equals("NUTRICIONISTA")) {
            Integer nutricionistaId = idNutricionista(correo);
            return aDetalle(citaRepository.findByNutricionistaIdOrderByFechaAscHoraAsc(nutricionistaId));
        }
        return aDetalle(citaRepository.findByPacienteIdOrderByFechaAscHoraAsc(usuarioId));
    }

    public CitaDetalleDTO buscar(Integer id, String rol, Integer usuarioId, String correo) {
        Cita cita = buscarPorId(id);
        if (!puedeVer(cita, rol, usuarioId, correo)) {
            throw new SecurityException("No puedes ver citas de otros usuarios");
        }
        return aDetalle(List.of(cita)).get(0);
    }

    public List<String> horasOcupadas(Integer nutricionistaId, LocalDate fecha) {
        List<String> horas = new ArrayList<>();
        for (Cita cita : citaRepository.findByNutricionistaIdAndFechaAndEstadoNot(nutricionistaId, fecha, CANCELADA)) {
            horas.add(cita.getHora());
        }
        return horas;
    }

    public Cita confirmar(Integer id, String rol, String correo) {
        Cita cita = buscarPorId(id);

        if (rol.equals("NUTRICIONISTA") && !cita.getNutricionistaId().equals(idNutricionista(correo))) {
            throw new SecurityException("Solo puedes confirmar citas de tu agenda");
        }
        if (!rol.equals("NUTRICIONISTA") && !rol.equals("ADMINISTRADOR")) {
            throw new SecurityException("Solo el nutricionista o el administrador confirman citas");
        }
        if (!cita.getEstado().equals(PENDIENTE)) {
            throw new IllegalArgumentException("Solo se pueden confirmar citas pendientes");
        }

        cita.setEstado(CONFIRMADA);
        return citaRepository.save(cita);
    }

    public Cita cancelar(Integer id, String rol, Integer usuarioId, String correo) {
        Cita cita = buscarPorId(id);

        if (!puedeVer(cita, rol, usuarioId, correo)) {
            throw new SecurityException("No puedes cancelar citas de otros usuarios");
        }
        if (cita.getEstado().equals(CANCELADA)) {
            throw new IllegalArgumentException("La cita ya está cancelada");
        }

        cita.setEstado(CANCELADA);
        return citaRepository.save(cita);
    }

    public Cita reprogramar(Integer id, LocalDate fecha, String hora, String rol, Integer usuarioId) {
        Cita cita = buscarPorId(id);

        boolean esDueno = rol.equals("PACIENTE") && cita.getPacienteId().equals(usuarioId);
        if (!esDueno && !rol.equals("ADMINISTRADOR")) {
            throw new SecurityException("Solo puedes reprogramar tus propias citas");
        }
        if (cita.getEstado().equals(CANCELADA)) {
            throw new IllegalArgumentException("No se puede reprogramar una cita cancelada");
        }
        validarHorario(cita.getNutricionistaId(), fecha, hora);

        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setEstado(PENDIENTE);
        return citaRepository.save(cita);
    }

    public void eliminar(Integer id) {
        if (!citaRepository.existsById(id)) {
            throw new RuntimeException("Cita no encontrada");
        }
        citaRepository.deleteById(id);
    }

    private Cita buscarPorId(Integer id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
    }

    private void validarHorario(Integer nutricionistaId, LocalDate fecha, String hora) {
        if (fecha == null || hora == null) {
            throw new IllegalArgumentException("Debe indicar fecha y hora");
        }
        if (fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser anterior a hoy");
        }
        if (!hora.matches("^([01][0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new IllegalArgumentException("La hora debe tener el formato HH:mm");
        }
        if (citaRepository.existsByNutricionistaIdAndFechaAndHoraAndEstadoNot(nutricionistaId, fecha, hora, CANCELADA)) {
            throw new IllegalArgumentException("El nutricionista ya tiene una cita en ese horario");
        }
    }

    private boolean puedeVer(Cita cita, String rol, Integer usuarioId, String correo) {
        if (rol.equals("ADMINISTRADOR")) {
            return true;
        }
        if (rol.equals("PACIENTE")) {
            return cita.getPacienteId().equals(usuarioId);
        }
        if (rol.equals("NUTRICIONISTA")) {
            return cita.getNutricionistaId().equals(idNutricionista(correo));
        }
        return false;
    }

    private Integer idNutricionista(String correo) {
        try {
            return catalogoClient.obtenerNutricionistaPorCorreo(correo).getId();
        } catch (Exception e) {
            throw new SecurityException("Tu usuario no está asociado a un nutricionista");
        }
    }

    private List<CitaDetalleDTO> aDetalle(List<Cita> citas) {
        List<CitaDetalleDTO> lista = new ArrayList<>();
        for (Cita cita : citas) {
            lista.add(new CitaDetalleDTO(cita.getId(), cita.getFecha(), cita.getHora(), cita.getMotivo(),
                    cita.getEstado(), cita.getPacienteId(), nombrePaciente(cita.getPacienteId()),
                    cita.getNutricionistaId(), nombreNutricionista(cita.getNutricionistaId())));
        }
        return lista;
    }

    private String nombrePaciente(Integer id) {
        try {
            UsuarioDTO usuario = usuarioClient.obtenerUsuario(id);
            return usuario.getNombre() + " " + usuario.getApellidos();
        } catch (Exception e) {
            return "Paciente " + id;
        }
    }

    private String nombreNutricionista(Integer id) {
        try {
            NutricionistaDTO nutricionista = catalogoClient.obtenerNutricionista(id);
            return nutricionista.getNombre();
        } catch (Exception e) {
            return "Nutricionista " + id;
        }
    }
}
