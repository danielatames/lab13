package com.medpharm.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receta_medica")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RecetaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_receta", nullable = false, unique = true, length = 30)
    private String codigoReceta;

    @Column(name = "paciente_nombre", nullable = false, length = 120)
    private String pacienteNombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    private Usuario medico;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, DESPACHADA, CANCELADA

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleReceta> detalles = new ArrayList<>();

    // Mantiene sincronizados ambos lados de la relación
    public void agregarDetalle(DetalleReceta detalle) {
        detalles.add(detalle);
        detalle.setReceta(this);
    }

    @PrePersist
    void prePersist() {
        if (fechaEmision == null) fechaEmision = LocalDateTime.now();
        if (estado == null) estado = "PENDIENTE";
    }
}