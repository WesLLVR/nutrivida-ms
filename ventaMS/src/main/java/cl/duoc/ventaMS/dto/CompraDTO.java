package cl.duoc.ventaMS.dto;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraDTO {

    private String nombre;
    private String run;
    private String correo;
    private String telefono;
    private String tipoTarjeta;
    private String numeroTarjeta;
    private Integer cuotas;
    private List<ItemDTO> items;
}
