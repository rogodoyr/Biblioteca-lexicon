package com.lexicon.report.service;

import com.lexicon.report.dto.ReportRequestDto;
import com.lexicon.report.dto.ReportResponseDto;
import com.lexicon.report.entity.Report;
import com.lexicon.report.repository.ReportRepository;
import com.lexicon.report.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    private Report report;
    private ReportRequestDto requestDto;

    @BeforeEach
    void setUp() {
        report = Report.builder()
                .id(1L)
                .build();

        requestDto = ReportRequestDto.builder()
                .build();
    }

    @Test
    void getAll_shouldReturnList() {
        when(reportRepository.findAll()).thenReturn(List.of(report));

        List<ReportResponseDto> result = reportService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnReport() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report));

        ReportResponseDto result = reportService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(reportRepository, times(1)).findById(1L);
    }

    @Test
    void getById_shouldThrowExceptionWhenNotFound() {
        when(reportRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reportService.getById(1L));
        verify(reportRepository, times(1)).findById(1L);
    }

    @Test
    void create_shouldReturnCreatedReport() {
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        ReportResponseDto result = reportService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        when(reportRepository.existsById(1L)).thenReturn(true);

        reportService.delete(1L);

        verify(reportRepository, times(1)).existsById(1L);
        verify(reportRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_shouldThrowExceptionWhenNotFound() {
        when(reportRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> reportService.delete(1L));
        verify(reportRepository, times(1)).existsById(1L);
        verify(reportRepository, never()).deleteById(anyLong());
    }
}
