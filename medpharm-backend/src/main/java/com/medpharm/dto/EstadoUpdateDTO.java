package com.medpharm.dto;

import jakarta.validation.constraints.*;

public record EstadoUpdateDTO(
        @NotBlank(message = "El estado es requerido")
        @Pattern(regexp = "DESPACHADA|CANCELADA", message = "Estado permitido: DESPACHADA o CANCELADA")
        String estado) {
            
        }