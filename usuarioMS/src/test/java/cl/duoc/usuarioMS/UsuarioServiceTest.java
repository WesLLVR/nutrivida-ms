package cl.duoc.usuarioMS;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import cl.duoc.usuarioMS.dto.RegistroDTO;
import cl.duoc.usuarioMS.dto.SesionDTO;
import cl.duoc.usuarioMS.model.Comuna;
import cl.duoc.usuarioMS.model.Region;
import cl.duoc.usuarioMS.model.Rol;
import cl.duoc.usuarioMS.model.Usuario;
import cl.duoc.usuarioMS.repository.ComunaRepository;
import cl.duoc.usuarioMS.repository.RolRepository;
import cl.duoc.usuarioMS.repository.UsuarioRepository;
import cl.duoc.usuarioMS.security.JwtUtil;
import cl.duoc.usuarioMS.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private ComunaRepository comunaRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario crearUsuario(boolean activo) {
        Comuna temuco = new Comuna(1, "Temuco", new Region(1, "La Araucanía"));
        String clave = new BCryptPasswordEncoder().encode("1234");
        return new Usuario(1, "198765430", "Matías", "Vergara", "usuario@correo.cl", clave,
                "987654321", "Los Robles 145", activo, temuco, new Rol(3, "PACIENTE"));
    }

    @Test
    void login_conClaveCorrecta_debeEntregarToken() {
        when(usuarioRepository.findByCorreo("usuario@correo.cl")).thenReturn(Optional.of(crearUsuario(true)));
        when(jwtUtil.generarToken(1, "usuario@correo.cl", "PACIENTE")).thenReturn("token-de-prueba");

        SesionDTO sesion = usuarioService.login("usuario@correo.cl", "1234");

        assertEquals("token-de-prueba", sesion.getToken());
        assertEquals("PACIENTE", sesion.getRol());
    }

    @Test
    void login_conClaveIncorrecta_debeLanzarError() {
        when(usuarioRepository.findByCorreo("usuario@correo.cl")).thenReturn(Optional.of(crearUsuario(true)));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.login("usuario@correo.cl", "9999"));

        assertEquals("El correo o la contraseña no coinciden", e.getMessage());
    }

    @Test
    void login_conCuentaDesactivada_debeLanzarError() {
        when(usuarioRepository.findByCorreo("usuario@correo.cl")).thenReturn(Optional.of(crearUsuario(false)));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.login("usuario@correo.cl", "1234"));

        assertEquals("Tu cuenta está desactivada", e.getMessage());
    }

    @Test
    void registrar_debeGuardarLaClaveEncriptadaConRolPaciente() {
        Comuna temuco = new Comuna(1, "Temuco", new Region(1, "La Araucanía"));
        when(usuarioRepository.existsByRun(any())).thenReturn(false);
        when(usuarioRepository.existsByCorreo(any())).thenReturn(false);
        when(comunaRepository.findByNombre("Temuco")).thenReturn(Optional.of(temuco));
        when(rolRepository.findByNombre("PACIENTE")).thenReturn(Optional.of(new Rol(3, "PACIENTE")));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario nuevo = usuarioService.registrar(new RegistroDTO("190876543", "Ana", "Pérez",
                "ana@correo.cl", "clave1", "912345678", "Temuco", "Prat 100"));

        assertNotEquals("clave1", nuevo.getClave());
        assertTrue(new BCryptPasswordEncoder().matches("clave1", nuevo.getClave()));
        assertEquals("PACIENTE", nuevo.getRol().getNombre());
    }

    @Test
    void registrar_conCorreoRepetido_debeLanzarError() {
        when(usuarioRepository.existsByRun(any())).thenReturn(false);
        when(usuarioRepository.existsByCorreo("usuario@correo.cl")).thenReturn(true);

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrar(new RegistroDTO("190876543", "Ana", "Pérez",
                        "usuario@correo.cl", "clave1", "912345678", "Temuco", "Prat 100")));

        assertEquals("El correo ya está registrado", e.getMessage());
    }

    @Test
    void cambiarEstado_debeDesactivarUnUsuarioActivo() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(crearUsuario(true)));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = usuarioService.cambiarEstado(1);

        assertFalse(resultado.getActivo());
    }
}
