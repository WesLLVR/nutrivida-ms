package cl.duoc.fichaMS.dto;

import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FichaDetalleDTO {

    private Integer id;
    private LocalDate fecha;
    private Double peso;
    private Double talla;
    private Double cintura;
    private Double imc;
    private String observaciones;
    private String planAlimenticio;
    private Integer pacienteId;
    private String paciente;
    private Integer nutricionistaId;
    private String nutricionista;
}
