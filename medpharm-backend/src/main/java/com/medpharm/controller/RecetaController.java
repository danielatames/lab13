package com.medpharm.controller;

import com.medpharm.dto.*;
import com.medpharm.service.RecetaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recetas")
public class RecetaController {

    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
    }

    @GetMapping
    public List<RecetaResponseDTO> listar() {
        return recetaService.listar();
    }

    @GetMapping("/estado/{estado}")
    public List<RecetaResponseDTO> listarPorEstado(@PathVariable String estado) {
        return recetaService.listarPorEstado(estado);
    }

    @PostMapping
    public ResponseEntity<RecetaResponseDTO> crear(@Valid @RequestBody RecetaRequestDTO dto,Authentication auth) {
        RecetaResponseDTO creada = recetaService.crear(dto, auth.getName());
        return ResponseEntity.created(URI.create("/api/v1/recetas/" + creada.id())).body(creada);
    }

    @PatchMapping("/{id}/estado")
    public RecetaResponseDTO cambiarEstado(@PathVariable Long id,@Valid @RequestBody EstadoUpdateDTO dto) {
        return recetaService.cambiarEstado(id, dto.estado());
    }
}