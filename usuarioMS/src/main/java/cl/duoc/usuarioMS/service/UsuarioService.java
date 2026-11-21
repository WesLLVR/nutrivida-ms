package cl.duoc.usuarioMS.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import cl.duoc.usuarioMS.dto.RegistroDTO;
import cl.duoc.usuarioMS.dto.SesionDTO;
import cl.duoc.usuarioMS.dto.UsuarioFormDTO;
import cl.duoc.usuarioMS.model.Comuna;
import cl.duoc.usuarioMS.model.Rol;
import cl.duoc.usuarioMS.model.Usuario;
import cl.duoc.usuarioMS.repository.ComunaRepository;
import cl.duoc.usuarioMS.repository.RolRepository;
import cl.duoc.usuarioMS.repository.UsuarioRepository;
import cl.duoc.usuarioMS.security.JwtUtil;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private ComunaRepository comunaRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encriptador = new BCryptPasswordEncoder();

    public SesionDTO login(String correo, String clave) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("El correo o la contraseña no coinciden"));

        if (!encriptador.matches(clave, usuario.getClave())) {
            throw new IllegalArgumentException("El correo o la contraseña no coinciden");
        }

        if (!usuario.getActivo()) {
            throw new IllegalArgumentException("Tu cuenta está desactivada");
        }

        return crearSesion(usuario);
    }

    public SesionDTO renovar(String correo) {
        Usuario usuario = buscarPorCorreo(correo);

        if (!usuario.getActivo()) {
            throw new IllegalArgumentException("Tu cuenta está desactivada");
        }

        return crearSesion(usuario);
    }

    public Usuario registrar(RegistroDTO datos) {
        UsuarioFormDTO form = new UsuarioFormDTO(datos.getRun(), datos.getNombre(), datos.getApellidos(),
                datos.getCorreo(), datos.getClave(), datos.getTelefono(), datos.getComuna(),
                datos.getDireccion(), "PACIENTE");
        return crear(form);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario crear(UsuarioFormDTO datos) {
        validarDatos(datos);

        if (datos.getClave() == null || datos.getClave().length() < 4 || datos.getClave().length() > 10) {
            throw new IllegalArgumentException("La contraseña debe tener entre 4 y 10 caracteres");
        }
        if (usuarioRepository.existsByRun(datos.getRun())) {
            throw new IllegalArgumentException("El RUN ya está registrado");
        }
        if (usuarioRepository.existsByCorreo(datos.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setRun(datos.getRun());
        usuario.setNombre(datos.getNombre());
        usuario.setApellidos(datos.getApellidos());
        usuario.setCorreo(datos.getCorreo());
        usuario.setClave(encriptador.encode(datos.getClave()));
        usuario.setTelefono(datos.getTelefono());
        usuario.setDireccion(datos.getDireccion());
        usuario.setComuna(buscarComuna(datos.getComuna()));
        usuario.setRol(buscarRol(datos.getRol()));
        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Integer id, UsuarioFormDTO datos) {
        Usuario usuario = buscarPorId(id);
        validarDatos(datos);

        usuarioRepository.findByCorreo(datos.getCorreo()).ifPresent(otro -> {
            if (!otro.getId().equals(id)) {
                throw new IllegalArgumentException("El correo ya está registrado");
            }
        });

        usuario.setRun(datos.getRun());
        usuario.setNombre(datos.getNombre());
        usuario.setApellidos(datos.getApellidos());
        usuario.setCorreo(datos.getCorreo());
        usuario.setTelefono(datos.getTelefono());
        usuario.setDireccion(datos.getDireccion());
        usuario.setComuna(buscarComuna(datos.getComuna()));
        usuario.setRol(buscarRol(datos.getRol()));

        if (datos.getClave() != null && !datos.getClave().isBlank()) {
            usuario.setClave(encriptador.encode(datos.getClave()));
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario cambiarEstado(Integer id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(!usuario.getActivo());
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    private SesionDTO crearSesion(Usuario usuario) {
        String token = jwtUtil.generarToken(usuario.getId(), usuario.getCorreo(), usuario.getRol().getNombre());
        return new SesionDTO(token, usuario.getId(), usuario.getNombre(), usuario.getApellidos(),
                usuario.getCorreo(), usuario.getRol().getNombre());
    }

    private void validarDatos(UsuarioFormDTO datos) {
        if (vacio(datos.getRun()) || vacio(datos.getNombre()) || vacio(datos.getApellidos())
                || vacio(datos.getCorreo()) || vacio(datos.getDireccion()) || vacio(datos.getComuna())) {
            throw new IllegalArgumentException("Faltan datos obligatorios");
        }
        if (!datos.getCorreo().matches("^[^@\\s]+@[^@\\s]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("El correo no tiene un formato válido");
        }
    }

    private Comuna buscarComuna(String nombre) {
        return comunaRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("La comuna no existe"));
    }

    private Rol buscarRol(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("El rol no existe"));
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
