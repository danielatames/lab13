package com.medpharm.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record RecetaRequestDTO(
        @NotBlank(message = "El nombre del paciente es requerido")
        @Size(min = 5, max = 120, message = "El nombre debe tener entre 5 y 120 caracteres")
        String pacienteNombre,

        @NotEmpty(message = "La receta debe tener al menos un medicamento")
        @Valid
        List<DetalleRequestDTO> detalles) {}