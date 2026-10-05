package com.medpharm.dto;

public record DetalleResponseDTO(Long id, Long medicamentoId, String medicamentoNombre,Integer cantidad, String dosisIndicada) {}