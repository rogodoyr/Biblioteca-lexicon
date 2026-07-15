package com.lexicon.report.service;

import com.lexicon.report.dto.ReportRequestDto;
import com.lexicon.report.dto.ReportResponseDto;
import com.lexicon.report.entity.Report;
import com.lexicon.report.repository.ReportRepository;
import com.lexicon.report.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestion de reportes y analiticas.
 * Proporciona operaciones CRUD completas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    /**
     * Obtiene todos los reportes del sistema.
     * @return Lista de reportes como DTOs de respuesta
     */
    @Transactional(readOnly = true)
    public List<ReportResponseDto> getAll() {
        log.info("Fetching all reports");
        return reportRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un reporte por su ID.
     * @param id Identificador unico del reporte
     * @return DTO de respuesta con los datos del reporte
     * @throws ResourceNotFoundException si no se encuentra el reporte
     */
    @Transactional(readOnly = true)
    public ReportResponseDto getById(Long id) {
        log.info("Fetching report with id: {}", id);
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Report not found with id: {}", id);
                    return new ResourceNotFoundException("Report not found with id: " + id);
                });
        return mapToResponseDto(report);
    }

    /**
     * Crea un nuevo reporte en el sistema.
     * @param requestDto DTO con los datos del reporte a crear
     * @return DTO de respuesta con el reporte creado y su ID generado
     */
    @Transactional
    public ReportResponseDto create(ReportRequestDto requestDto) {
        log.info("Creating new report");
        Report report = Report.builder()
                .reportType(requestDto.getReportType())
                .generatedBy(requestDto.getGeneratedBy())
                .parameters(requestDto.getParameters())
                .result(requestDto.getResult())
                .createdAt(requestDto.getCreatedAt())
                .build();
        Report savedReport = reportRepository.save(report);
        log.info("Report created successfully with id: {}", savedReport.getId());
        return mapToResponseDto(savedReport);
    }

    /**
     * Actualiza un reporte existente.
     * @param id Identificador del reporte a actualizar
     * @param requestDto DTO con los nuevos datos del reporte
     * @return DTO de respuesta con el reporte actualizado
     * @throws ResourceNotFoundException si no se encuentra el reporte
     */
    @Transactional
    public ReportResponseDto update(Long id, ReportRequestDto requestDto) {
        log.info("Updating report with id: {}", id);
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Report not found with id: {}", id);
                    return new ResourceNotFoundException("Report not found with id: " + id);
                });

        report.setReportType(requestDto.getReportType());
        report.setGeneratedBy(requestDto.getGeneratedBy());
        report.setParameters(requestDto.getParameters());
        report.setResult(requestDto.getResult());
        report.setCreatedAt(requestDto.getCreatedAt());
        Report updatedReport = reportRepository.save(report);
        log.info("Report updated successfully with id: {}", updatedReport.getId());
        return mapToResponseDto(updatedReport);
    }

    /**
     * Elimina un reporte del sistema.
     * @param id Identificador del reporte a eliminar
     * @throws ResourceNotFoundException si no se encuentra el reporte
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting report with id: {}", id);
        if (!reportRepository.existsById(id)) {
            log.error("Report not found with id: {}", id);
            throw new ResourceNotFoundException("Report not found with id: " + id);
        }
        reportRepository.deleteById(id);
        log.info("Report deleted successfully with id: {}", id);
    }

    private ReportResponseDto mapToResponseDto(Report report) {
        return ReportResponseDto.builder()
                .id(report.getId())
                .reportType(report.getReportType())
                .generatedBy(report.getGeneratedBy())
                .parameters(report.getParameters())
                .result(report.getResult())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
