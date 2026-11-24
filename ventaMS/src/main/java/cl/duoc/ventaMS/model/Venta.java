package cl.duoc.ventaMS.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Integer pacienteId;

    @Column(nullable = false)
    private String cliente;

    @Column(nullable = false)
    private String run;

    @Column(nullable = false)
    private String correo;

    private String telefono;

    @Column(nullable = false)
    private String tipoTarjeta;

    @Column(nullable = false)
    private String tarjeta;

    @Column(nullable = false)
    private Integer cuotas;

    @Column(nullable = false)
    private Integer neto;

    @Column(nullable = false)
    private Integer iva;

    @Column(nullable = false)
    private Integer total;

    @Column(nullable = false)
    private String estado;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalle = new ArrayList<>();
}
