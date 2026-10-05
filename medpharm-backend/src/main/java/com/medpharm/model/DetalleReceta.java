package com.medpharm.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_receta")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class DetalleReceta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receta_id", nullable = false)
    private RecetaMedica receta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicamento_id", nullable = false)
    private Medicamento medicamento;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "dosis_indicada", nullable = false, length = 255)
    private String dosisIndicada;
}