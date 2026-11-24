package cl.duoc.citaMS.dto;

import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaDetalleDTO {

    private Integer id;
    private LocalDate fecha;
    private String hora;
    private String motivo;
    private String estado;
    private Integer pacienteId;
    private String paciente;
    private Integer nutricionistaId;
    private String nutricionista;
}
