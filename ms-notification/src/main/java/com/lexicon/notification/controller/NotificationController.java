package com.lexicon.notification.controller;

import com.lexicon.notification.dto.NotificationRequestDto;
import com.lexicon.notification.dto.NotificationResponseDto;
import com.lexicon.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification API", description = "CRUD operations for Notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Get all notifications", description = "Retrieves a list of all notifications")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getAll() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    @Operation(summary = "Get a notification by ID", description = "Retrieves a specific notification by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification found"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getById(id));
    }

    @Operation(summary = "Create a new notification", description = "Creates a new notification record")
    @ApiResponse(responseCode = "201", description = "Notification created successfully")
    @PostMapping
    public ResponseEntity<NotificationResponseDto> create(@RequestBody NotificationRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(requestDto));
    }

    @Operation(summary = "Update an existing notification", description = "Updates the details of an existing notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification updated successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> update(@PathVariable Long id, @RequestBody NotificationRequestDto requestDto) {
        return ResponseEntity.ok(notificationService.update(id, requestDto));
    }

    @Operation(summary = "Delete a notification", description = "Deletes a notification by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notification deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
