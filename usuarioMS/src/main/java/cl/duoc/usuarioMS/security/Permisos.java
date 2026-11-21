package cl.duoc.usuarioMS.security;

import jakarta.servlet.http.HttpServletRequest;

public class Permisos {

    public static boolean tieneRol(HttpServletRequest request, String... roles) {
        Object rol = request.getAttribute("rol");
        if (rol == null) {
            return false;
        }
        for (String permitido : roles) {
            if (permitido.equals(rol)) {
                return true;
            }
        }
        return false;
    }

    public static Integer usuarioId(HttpServletRequest request) {
        return (Integer) request.getAttribute("usuarioId");
    }

    public static String correo(HttpServletRequest request) {
        return (String) request.getAttribute("correo");
    }
}
