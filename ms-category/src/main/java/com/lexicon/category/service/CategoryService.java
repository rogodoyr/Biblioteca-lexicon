package com.lexicon.category.service;

import com.lexicon.category.dto.CategoryRequestDto;
import com.lexicon.category.dto.CategoryResponseDto;
import com.lexicon.category.entity.Category;
import com.lexicon.category.repository.CategoryRepository;
import com.lexicon.category.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestion de categorias de libros.
 * Proporciona operaciones CRUD completas.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Obtiene todas las categorias del sistema.
     * @return Lista de categorias como DTOs de respuesta
     */
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAll() {
        log.info("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una categoria por su ID.
     * @param id Identificador unico de la categoria
     * @return DTO de respuesta con los datos de la categoria
     * @throws ResourceNotFoundException si no se encuentra la categoria
     */
    @Transactional(readOnly = true)
    public CategoryResponseDto getById(Long id) {
        log.info("Fetching category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", id);
                    return new ResourceNotFoundException("Category not found with id: " + id);
                });
        return mapToResponseDto(category);
    }

    /**
     * Crea una nueva categoria en el sistema.
     * @param requestDto DTO con los datos de la categoria a crear
     * @return DTO de respuesta con la categoria creada y su ID generado
     */
    @Transactional
    public CategoryResponseDto create(CategoryRequestDto requestDto) {
        log.info("Creating new category");
        Category category = Category.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .build();
        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with id: {}", savedCategory.getId());
        return mapToResponseDto(savedCategory);
    }

    /**
     * Actualiza una categoria existente.
     * @param id Identificador de la categoria a actualizar
     * @param requestDto DTO con los nuevos datos de la categoria
     * @return DTO de respuesta con la categoria actualizada
     * @throws ResourceNotFoundException si no se encuentra la categoria
     */
    @Transactional
    public CategoryResponseDto update(Long id, CategoryRequestDto requestDto) {
        log.info("Updating category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", id);
                    return new ResourceNotFoundException("Category not found with id: " + id);
                });

        category.setName(requestDto.getName());
        category.setDescription(requestDto.getDescription());
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully with id: {}", updatedCategory.getId());
        return mapToResponseDto(updatedCategory);
    }

    /**
     * Elimina una categoria del sistema.
     * @param id Identificador de la categoria a eliminar
     * @throws ResourceNotFoundException si no se encuentra la categoria
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting category with id: {}", id);
        if (!categoryRepository.existsById(id)) {
            log.error("Category not found with id: {}", id);
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
        log.info("Category deleted successfully with id: {}", id);
    }

    private CategoryResponseDto mapToResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
