package cl.duoc.usuarioMS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SesionDTO {

    private String token;
    private Integer id;
    private String nombre;
    private String apellidos;
    private String correo;
    private String rol;
}
