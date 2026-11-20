package cl.duoc.catalogoMS.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "servicio")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String duracion;

    @Column(nullable = false)
    private String modalidad;

    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false, length = 300)
    private String descripcion;

    private String imagen;

    @Column(nullable = false)
    private Boolean destacado;

    @ManyToOne
    @JoinColumn(name = "tipo_servicio_id", nullable = false)
    private TipoServicio tipo;
}
