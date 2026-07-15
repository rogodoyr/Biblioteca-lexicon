package com.lexicon.notification.service;

import com.lexicon.notification.dto.NotificationRequestDto;
import com.lexicon.notification.dto.NotificationResponseDto;
import com.lexicon.notification.entity.Notification;
import com.lexicon.notification.repository.NotificationRepository;
import com.lexicon.notification.exception.ResourceNotFoundException;
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
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;
    private NotificationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        notification = Notification.builder()
                .id(1L)
                .build();

        requestDto = NotificationRequestDto.builder()
                .build();
    }

    @Test
    void getAll_shouldReturnList() {
        when(notificationRepository.findAll()).thenReturn(List.of(notification));

        List<NotificationResponseDto> result = notificationService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    void getById_shouldReturnNotification() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        NotificationResponseDto result = notificationService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void getById_shouldThrowExceptionWhenNotFound() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.getById(1L));
        verify(notificationRepository, times(1)).findById(1L);
    }

    @Test
    void create_shouldReturnCreatedNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        NotificationResponseDto result = notificationService.create(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        when(notificationRepository.existsById(1L)).thenReturn(true);

        notificationService.delete(1L);

        verify(notificationRepository, times(1)).existsById(1L);
        verify(notificationRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_shouldThrowExceptionWhenNotFound() {
        when(notificationRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> notificationService.delete(1L));
        verify(notificationRepository, times(1)).existsById(1L);
        verify(notificationRepository, never()).deleteById(anyLong());
    }
}
