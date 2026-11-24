package cl.duoc.fichaMS.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.fichaMS.client.CatalogoClient;
import cl.duoc.fichaMS.client.UsuarioClient;
import cl.duoc.fichaMS.dto.FichaDetalleDTO;
import cl.duoc.fichaMS.dto.UsuarioDTO;
import cl.duoc.fichaMS.model.Ficha;
import cl.duoc.fichaMS.repository.FichaRepository;

@Service
public class FichaService {

    @Autowired
    private FichaRepository fichaRepository;

    @Autowired
    private CatalogoClient catalogoClient;

    @Autowired
    private UsuarioClient usuarioClient;

    public double calcularImc(double peso, double talla) {
        double imc = peso / (talla * talla);
        return Math.round(imc * 10) / 10.0;
    }

    public Ficha crear(Ficha ficha, String correoNutricionista) {
        validar(ficha);
        try {
            usuarioClient.obtenerUsuario(ficha.getPacienteId());
        } catch (Exception e) {
            throw new IllegalArgumentException("El paciente no existe");
        }

        ficha.setId(null);
        ficha.setNutricionistaId(idNutricionista(correoNutricionista));
        ficha.setImc(calcularImc(ficha.getPeso(), ficha.getTalla()));
        return fichaRepository.save(ficha);
    }

    public List<FichaDetalleDTO> listarTodas() {
        return aDetalle(fichaRepository.findAll());
    }

    public List<FichaDetalleDTO> misFichas(String rol, Integer usuarioId, String correo) {
        if (rol.equals("NUTRICIONISTA")) {
            return aDetalle(fichaRepository.findByNutricionistaIdOrderByFechaDesc(idNutricionista(correo)));
        }
        return aDetalle(fichaRepository.findByPacienteIdOrderByFechaDesc(usuarioId));
    }

    public FichaDetalleDTO buscar(Integer id, String rol, Integer usuarioId, String correo) {
        Ficha ficha = buscarPorId(id);
        boolean permitido = rol.equals("ADMINISTRADOR")
                || (rol.equals("PACIENTE") && ficha.getPacienteId().equals(usuarioId))
                || (rol.equals("NUTRICIONISTA") && ficha.getNutricionistaId().equals(idNutricionista(correo)));
        if (!permitido) {
            throw new SecurityException("No puedes ver fichas de otros pacientes");
        }
        return aDetalle(List.of(ficha)).get(0);
    }

    public Ficha actualizar(Integer id, Ficha datos, String correo) {
        Ficha ficha = buscarPorId(id);
        if (!ficha.getNutricionistaId().equals(idNutricionista(correo))) {
            throw new SecurityException("Solo el nutricionista que creó la ficha puede editarla");
        }
        validar(datos);

        ficha.setFecha(datos.getFecha());
        ficha.setPeso(datos.getPeso());
        ficha.setTalla(datos.getTalla());
        ficha.setCintura(datos.getCintura());
        ficha.setObservaciones(datos.getObservaciones());
        ficha.setPlanAlimenticio(datos.getPlanAlimenticio());
        ficha.setImc(calcularImc(datos.getPeso(), datos.getTalla()));
        return fichaRepository.save(ficha);
    }

    public void eliminar(Integer id, String rol, String correo) {
        Ficha ficha = buscarPorId(id);
        if (rol.equals("NUTRICIONISTA") && !ficha.getNutricionistaId().equals(idNutricionista(correo))) {
            throw new SecurityException("Solo el nutricionista que creó la ficha puede eliminarla");
        }
        fichaRepository.deleteById(id);
    }

    private Ficha buscarPorId(Integer id) {
        return fichaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ficha no encontrada"));
    }

    private void validar(Ficha ficha) {
        if (ficha.getPacienteId() == null || ficha.getFecha() == null) {
            throw new IllegalArgumentException("Debe indicar el paciente y la fecha");
        }
        if (ficha.getPeso() == null || ficha.getPeso() < 2 || ficha.getPeso() > 300) {
            throw new IllegalArgumentException("El peso debe estar entre 2 y 300 kg");
        }
        if (ficha.getTalla() == null || ficha.getTalla() < 0.4 || ficha.getTalla() > 2.5) {
            throw new IllegalArgumentException("La talla debe estar entre 0,40 y 2,50 metros");
        }
    }

    private Integer idNutricionista(String correo) {
        try {
            return catalogoClient.obtenerNutricionistaPorCorreo(correo).getId();
        } catch (Exception e) {
            throw new SecurityException("Tu usuario no está asociado a un nutricionista");
        }
    }

    private List<FichaDetalleDTO> aDetalle(List<Ficha> fichas) {
        List<FichaDetalleDTO> lista = new ArrayList<>();
        for (Ficha f : fichas) {
            lista.add(new FichaDetalleDTO(f.getId(), f.getFecha(), f.getPeso(), f.getTalla(), f.getCintura(),
                    f.getImc(), f.getObservaciones(), f.getPlanAlimenticio(), f.getPacienteId(),
                    nombrePaciente(f.getPacienteId()), f.getNutricionistaId(), nombreNutricionista(f.getNutricionistaId())));
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
            return catalogoClient.obtenerNutricionista(id).getNombre();
        } catch (Exception e) {
            return "Nutricionista " + id;
        }
    }
}
