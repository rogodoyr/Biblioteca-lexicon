package com.lexicon.notification.service;

import com.lexicon.notification.dto.NotificationRequestDto;
import com.lexicon.notification.dto.NotificationResponseDto;
import com.lexicon.notification.entity.Notification;
import com.lexicon.notification.repository.NotificationRepository;
import com.lexicon.notification.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestion de notificaciones del sistema.
 * Proporciona operaciones CRUD completas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Obtiene todas las notificaciones del sistema.
     * @return Lista de notificaciones como DTOs de respuesta
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getAll() {
        log.info("Fetching all notifications");
        return notificationRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una notificacion por su ID.
     * @param id Identificador unico de la notificacion
     * @return DTO de respuesta con los datos de la notificacion
     * @throws ResourceNotFoundException si no se encuentra la notificacion
     */
    @Transactional(readOnly = true)
    public NotificationResponseDto getById(Long id) {
        log.info("Fetching notification with id: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Notification not found with id: {}", id);
                    return new ResourceNotFoundException("Notification not found with id: " + id);
                });
        return mapToResponseDto(notification);
    }

    /**
     * Crea una nueva notificacion en el sistema.
     * @param requestDto DTO con los datos de la notificacion a crear
     * @return DTO de respuesta con la notificacion creada y su ID generado
     */
    @Transactional
    public NotificationResponseDto create(NotificationRequestDto requestDto) {
        log.info("Creating new notification");
        Notification notification = Notification.builder()
                .userId(requestDto.getUserId())
                .type(requestDto.getType())
                .message(requestDto.getMessage())
                .readStatus(requestDto.getReadStatus())
                .createdAt(requestDto.getCreatedAt())
                .build();
        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created successfully with id: {}", savedNotification.getId());
        return mapToResponseDto(savedNotification);
    }

    /**
     * Actualiza una notificacion existente.
     * @param id Identificador de la notificacion a actualizar
     * @param requestDto DTO con los nuevos datos de la notificacion
     * @return DTO de respuesta con la notificacion actualizada
     * @throws ResourceNotFoundException si no se encuentra la notificacion
     */
    @Transactional
    public NotificationResponseDto update(Long id, NotificationRequestDto requestDto) {
        log.info("Updating notification with id: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Notification not found with id: {}", id);
                    return new ResourceNotFoundException("Notification not found with id: " + id);
                });

        notification.setUserId(requestDto.getUserId());
        notification.setType(requestDto.getType());
        notification.setMessage(requestDto.getMessage());
        notification.setReadStatus(requestDto.getReadStatus());
        notification.setCreatedAt(requestDto.getCreatedAt());
        Notification updatedNotification = notificationRepository.save(notification);
        log.info("Notification updated successfully with id: {}", updatedNotification.getId());
        return mapToResponseDto(updatedNotification);
    }

    /**
     * Elimina una notificacion del sistema.
     * @param id Identificador de la notificacion a eliminar
     * @throws ResourceNotFoundException si no se encuentra la notificacion
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting notification with id: {}", id);
        if (!notificationRepository.existsById(id)) {
            log.error("Notification not found with id: {}", id);
            throw new ResourceNotFoundException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
        log.info("Notification deleted successfully with id: {}", id);
    }

    private NotificationResponseDto mapToResponseDto(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .message(notification.getMessage())
                .readStatus(notification.getReadStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
