package cl.duoc.usuarioMS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDTO {

    private String run;
    private String nombre;
    private String apellidos;
    private String correo;
    private String clave;
    private String telefono;
    private String comuna;
    private String direccion;
}
