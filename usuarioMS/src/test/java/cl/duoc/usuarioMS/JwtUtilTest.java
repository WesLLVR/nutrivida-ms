package cl.duoc.usuarioMS;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import cl.duoc.usuarioMS.security.JwtUtil;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void preparar() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secreto", "ClaveDePruebaParaLosTests");
        ReflectionTestUtils.setField(jwtUtil, "minutos", 60L);
    }

    @Test
    void generarToken_debeTenerTresPartesYGuardarElRol() {
        String token = jwtUtil.generarToken(1, "admin@nutrivida.cl", "ADMINISTRADOR");

        Map<String, Object> datos = jwtUtil.validarToken(token);

        assertEquals(3, token.split("\\.").length);
        assertEquals("admin@nutrivida.cl", datos.get("sub"));
        assertEquals("ADMINISTRADOR", datos.get("rol"));
    }

    @Test
    void validarToken_modificado_debeRetornarNull() {
        String token = jwtUtil.generarToken(2, "usuario@correo.cl", "PACIENTE");
        String[] partes = token.split("\\.");
        String falso = partes[0] + "." + partes[1] + "x." + partes[2];

        assertNull(jwtUtil.validarToken(falso));
    }

    @Test
    void validarToken_expirado_debeRetornarNull() {
        ReflectionTestUtils.setField(jwtUtil, "minutos", -1L);
        String token = jwtUtil.generarToken(2, "usuario@correo.cl", "PACIENTE");

        assertNull(jwtUtil.validarToken(token));
    }
}
