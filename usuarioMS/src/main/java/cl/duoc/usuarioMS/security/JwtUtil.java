package cl.duoc.usuarioMS.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JwtUtil {

    @Value("${jwt.secreto}")
    private String secreto;

    @Value("${jwt.minutos}")
    private long minutos;

    private final ObjectMapper mapper = new ObjectMapper();

    public String generarToken(Integer id, String correo, String rol) {
        long ahora = System.currentTimeMillis() / 1000;

        Map<String, Object> encabezado = new LinkedHashMap<>();
        encabezado.put("alg", "HS256");
        encabezado.put("typ", "JWT");

        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put("sub", correo);
        datos.put("id", id);
        datos.put("rol", rol);
        datos.put("iat", ahora);
        datos.put("exp", ahora + minutos * 60);

        try {
            String parte1 = codificar(mapper.writeValueAsBytes(encabezado));
            String parte2 = codificar(mapper.writeValueAsBytes(datos));
            return parte1 + "." + parte2 + "." + firmar(parte1 + "." + parte2);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo generar el token");
        }
    }

    public Map<String, Object> validarToken(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                return null;
            }

            String firmaEsperada = firmar(partes[0] + "." + partes[1]);
            if (!MessageDigest.isEqual(firmaEsperada.getBytes(), partes[2].getBytes())) {
                return null;
            }

            byte[] json = Base64.getUrlDecoder().decode(partes[1]);
            Map<String, Object> datos = mapper.readValue(json, Map.class);

            long exp = ((Number) datos.get("exp")).longValue();
            if (exp < System.currentTimeMillis() / 1000) {
                return null;
            }

            return datos;
        } catch (Exception e) {
            return null;
        }
    }

    private String firmar(String contenido) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return codificar(mac.doFinal(contenido.getBytes(StandardCharsets.UTF_8)));
    }

    private String codificar(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
