package com.medpharm.service;

import com.medpharm.dto.*;
import com.medpharm.exception.RecursoNoEncontradoException;
import com.medpharm.exception.ReglaNegocioException;
import com.medpharm.model.*;
import com.medpharm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.*;

@Service
public class RecetaService {

    private static final Set<String> ESTADOS = Set.of("PENDIENTE", "DESPACHADA", "CANCELADA");

    private final RecetaMedicaRepository recetaRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final UsuarioRepository usuarioRepository;

    public RecetaService(RecetaMedicaRepository recetaRepository,
                         MedicamentoRepository medicamentoRepository,
                         UsuarioRepository usuarioRepository) {
        this.recetaRepository = recetaRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> listar() {
        return recetaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<RecetaResponseDTO> listarPorEstado(String estado) {
        String e = estado.toUpperCase();
        if (!ESTADOS.contains(e)) {
            throw new IllegalArgumentException("Estado inválido: " + estado+ ". Valores permitidos: " + ESTADOS);
        }
        return recetaRepository.findByEstado(e).stream().map(this::toDTO).toList();
    }

    @Transactional
    public RecetaResponseDTO crear(RecetaRequestDTO dto, String username) {
        Usuario medico = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + username));

        RecetaMedica receta = new RecetaMedica();
        receta.setCodigoReceta(generarCodigo());
        receta.setPacienteNombre(dto.pacienteNombre().trim());
        receta.setMedico(medico);
        receta.setEstado("PENDIENTE");

        Map<Long, Integer> totalPorMedicamento = new HashMap<>();

        for (DetalleRequestDTO d : dto.detalles()) {
            Medicamento m = medicamentoRepository.findById(d.medicamentoId()).orElseThrow(() -> new RecursoNoEncontradoException("Medicamento no encontrado: id " + d.medicamentoId()));

            int total = totalPorMedicamento.merge(m.getId(), d.cantidad(), Integer::sum);
            if (total > m.getStock()) {
                throw new ReglaNegocioException("Stock insuficiente para %s: disponible %d, solicitado %d"
                        .formatted(m.getNombre(), m.getStock(), total));
            }

            DetalleReceta detalle = new DetalleReceta();
            detalle.setMedicamento(m);
            detalle.setCantidad(d.cantidad());
            detalle.setDosisIndicada(d.dosisIndicada().trim());
            receta.agregarDetalle(detalle);
        }

        return toDTO(recetaRepository.save(receta));
    }

    @Transactional
    public RecetaResponseDTO cambiarEstado(Long id, String nuevoEstado) {
        RecetaMedica receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Receta no encontrada: id " + id));

        if (!"PENDIENTE".equals(receta.getEstado())) {
            throw new ReglaNegocioException("Solo se pueden modificar recetas PENDIENTES. Estado actual: "
                    + receta.getEstado());
        }

        if ("DESPACHADA".equals(nuevoEstado)) {
            for (DetalleReceta d : receta.getDetalles()) {
                Medicamento m = d.getMedicamento();
                if (m.getStock() < d.getCantidad()) {
                    throw new ReglaNegocioException("Stock insuficiente para despachar " + m.getNombre());
                }
                m.setStock(m.getStock() - d.getCantidad());
            }
        }

        receta.setEstado(nuevoEstado);
        return toDTO(receta);
    }

    private String generarCodigo() {
        int anio = Year.now().getValue();
        long n = recetaRepository.count() + 1;
        String codigo;
        do {
            codigo = "REC-%d-%03d".formatted(anio, n++);
        } while (recetaRepository.existsByCodigoReceta(codigo));
        return codigo;
    }

    private RecetaResponseDTO toDTO(RecetaMedica r) {
        List<DetalleResponseDTO> detalles = r.getDetalles().stream()
                .map(d -> new DetalleResponseDTO(d.getId(), d.getMedicamento().getId(),
                        d.getMedicamento().getNombre(), d.getCantidad(), d.getDosisIndicada()))
                .toList();

        return new RecetaResponseDTO(r.getId(), r.getCodigoReceta(), r.getPacienteNombre(),
                r.getMedico().getNombreCompleto(), r.getEstado(), r.getFechaEmision(), detalles);
    }
}