package com.medpharm.dto;

import jakarta.validation.constraints.*;

public record DetalleRequestDTO(
        @NotNull(message = "El medicamento es requerido")
        Long medicamentoId,

        @NotNull(message = "La cantidad es requerida")
        @Positive(message = "La cantidad debe ser mayor a 0")
        Integer cantidad,

        @NotBlank(message = "La dosis indicada es requerida")
        @Size(max = 255)
        String dosisIndicada) {
            
        }