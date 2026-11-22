package cl.duoc.fichaMS.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ficha")
public class Ficha {

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
    private Double peso;

    @Column(nullable = false)
    private Double talla;

    private Double cintura;

    @Column(nullable = false)
    private Double imc;

    @Column(length = 500)
    private String observaciones;

    @Column(length = 1000)
    private String planAlimenticio;
}
