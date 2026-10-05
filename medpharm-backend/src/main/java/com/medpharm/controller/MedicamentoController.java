package com.medpharm.controller;

import com.medpharm.dto.MedicamentoDTO;
import com.medpharm.service.MedicamentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medicamentos")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    public MedicamentoController(MedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @GetMapping
    public List<MedicamentoDTO> listar() {
        return medicamentoService.listar();
    }
}