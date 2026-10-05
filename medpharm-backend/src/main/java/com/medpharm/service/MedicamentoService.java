package com.medpharm.service;

import com.medpharm.dto.MedicamentoDTO;
import com.medpharm.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    public MedicamentoService(MedicamentoRepository medicamentoRepository) {
        this.medicamentoRepository = medicamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<MedicamentoDTO> listar() {
        return medicamentoRepository.findAll().stream().map(m -> new MedicamentoDTO(m.getId(), m.getCodigo(), m.getNombre(), m.getStock(), m.getPrecioUnitario())).toList();
    }
}