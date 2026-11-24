package cl.duoc.fichaMS.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFiltro extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String metodo = request.getMethod();
        String ruta = request.getRequestURI();

        if (!ruta.startsWith("/api/") || metodo.equals("OPTIONS") || esPublica(metodo, ruta)) {
            chain.doFilter(request, response);
            return;
        }

        String encabezado = request.getHeader("Authorization");
        if (encabezado == null || !encabezado.startsWith("Bearer ")) {
            responderError(response, "Debe iniciar sesión para usar este recurso");
            return;
        }

        Map<String, Object> datos = jwtUtil.validarToken(encabezado.substring(7));
        if (datos == null) {
            responderError(response, "Token inválido o expirado");
            return;
        }

        request.setAttribute("usuarioId", ((Number) datos.get("id")).intValue());
        request.setAttribute("correo", datos.get("sub"));
        request.setAttribute("rol", datos.get("rol"));

        chain.doFilter(request, response);
    }

    private boolean esPublica(String metodo, String ruta) {

        return false;
    }

    private void responderError(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensaje\": \"" + mensaje + "\"}");
    }
}
