package cl.duoc.fichaMS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutricionistaDTO {

    private Integer id;
    private String nombre;
    private String especialidad;
    private String correo;
}
