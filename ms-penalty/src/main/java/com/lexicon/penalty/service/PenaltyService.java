package com.lexicon.penalty.service;

import com.lexicon.penalty.dto.PenaltyRequestDto;
import com.lexicon.penalty.dto.PenaltyResponseDto;
import com.lexicon.penalty.entity.Penalty;
import com.lexicon.penalty.repository.PenaltyRepository;
import com.lexicon.penalty.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestion de multas y penalizaciones.
 * Proporciona operaciones CRUD completas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;

    /**
     * Obtiene todas las multas del sistema.
     * @return Lista de multas como DTOs de respuesta
     */
    @Transactional(readOnly = true)
    public List<PenaltyResponseDto> getAll() {
        log.info("Fetching all penalties");
        return penaltyRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una multa por su ID.
     * @param id Identificador unico de la multa
     * @return DTO de respuesta con los datos de la multa
     * @throws ResourceNotFoundException si no se encuentra la multa
     */
    @Transactional(readOnly = true)
    public PenaltyResponseDto getById(Long id) {
        log.info("Fetching penalty with id: {}", id);
        Penalty penalty = penaltyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Penalty not found with id: {}", id);
                    return new ResourceNotFoundException("Penalty not found with id: " + id);
                });
        return mapToResponseDto(penalty);
    }

    /**
     * Crea una nueva multa en el sistema.
     * @param requestDto DTO con los datos de la multa a crear
     * @return DTO de respuesta con la multa creada y su ID generado
     */
    @Transactional
    public PenaltyResponseDto create(PenaltyRequestDto requestDto) {
        log.info("Creating new penalty");
        Penalty penalty = Penalty.builder()
                .loanId(requestDto.getLoanId())
                .userId(requestDto.getUserId())
                .amount(requestDto.getAmount())
                .reason(requestDto.getReason())
                .status(requestDto.getStatus())
                .createdAt(requestDto.getCreatedAt())
                .build();
        Penalty savedPenalty = penaltyRepository.save(penalty);
        log.info("Penalty created successfully with id: {}", savedPenalty.getId());
        return mapToResponseDto(savedPenalty);
    }

    /**
     * Actualiza una multa existente.
     * @param id Identificador de la multa a actualizar
     * @param requestDto DTO con los nuevos datos de la multa
     * @return DTO de respuesta con la multa actualizada
     * @throws ResourceNotFoundException si no se encuentra la multa
     */
    @Transactional
    public PenaltyResponseDto update(Long id, PenaltyRequestDto requestDto) {
        log.info("Updating penalty with id: {}", id);
        Penalty penalty = penaltyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Penalty not found with id: {}", id);
                    return new ResourceNotFoundException("Penalty not found with id: " + id);
                });

        penalty.setLoanId(requestDto.getLoanId());
        penalty.setUserId(requestDto.getUserId());
        penalty.setAmount(requestDto.getAmount());
        penalty.setReason(requestDto.getReason());
        penalty.setStatus(requestDto.getStatus());
        penalty.setCreatedAt(requestDto.getCreatedAt());
        Penalty updatedPenalty = penaltyRepository.save(penalty);
        log.info("Penalty updated successfully with id: {}", updatedPenalty.getId());
        return mapToResponseDto(updatedPenalty);
    }

    /**
     * Elimina una multa del sistema.
     * @param id Identificador de la multa a eliminar
     * @throws ResourceNotFoundException si no se encuentra la multa
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting penalty with id: {}", id);
        if (!penaltyRepository.existsById(id)) {
            log.error("Penalty not found with id: {}", id);
            throw new ResourceNotFoundException("Penalty not found with id: " + id);
        }
        penaltyRepository.deleteById(id);
        log.info("Penalty deleted successfully with id: {}", id);
    }

    private PenaltyResponseDto mapToResponseDto(Penalty penalty) {
        return PenaltyResponseDto.builder()
                .id(penalty.getId())
                .loanId(penalty.getLoanId())
                .userId(penalty.getUserId())
                .amount(penalty.getAmount())
                .reason(penalty.getReason())
                .status(penalty.getStatus())
                .createdAt(penalty.getCreatedAt())
                .build();
    }
}
