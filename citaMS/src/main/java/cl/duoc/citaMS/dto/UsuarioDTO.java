package cl.duoc.citaMS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Integer id;
    private String nombre;
    private String apellidos;
    private String correo;
}
