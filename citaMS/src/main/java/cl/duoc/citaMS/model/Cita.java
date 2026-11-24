package cl.duoc.citaMS.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer pacienteId;

    @Column(nullable = false)
    private Integer nutricionistaId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String hora;

    @Column(nullable = false, length = 300)
    private String motivo;

    @Column(nullable = false)
    private String estado;
}
