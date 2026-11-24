package cl.duoc.ventaMS.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioDTO {

    private Integer id;
    private String codigo;
    private String nombre;
    private Integer precio;
}
