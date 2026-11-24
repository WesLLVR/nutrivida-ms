package cl.duoc.citaMS.dto;

import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudCitaDTO {

    private Integer nutricionistaId;
    private LocalDate fecha;
    private String hora;
    private String motivo;
}
