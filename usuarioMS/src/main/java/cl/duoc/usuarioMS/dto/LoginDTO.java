package cl.duoc.usuarioMS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    private String correo;
    private String clave;
}
