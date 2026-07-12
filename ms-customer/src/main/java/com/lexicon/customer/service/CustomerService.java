package com.lexicon.customer.service;

import com.lexicon.customer.dto.CustomerRequestDto;
import com.lexicon.customer.dto.CustomerResponseDto;
import com.lexicon.customer.entity.Customer;
import com.lexicon.customer.exception.CustomerNotFoundException;
import com.lexicon.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestion de clientes.
 * Proporciona operaciones CRUD completas sobre los clientes de la biblioteca.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Crea un nuevo cliente en el sistema.
     * @param requestDto DTO con los datos del cliente a crear
     * @return DTO de respuesta con el cliente creado y su ID generado
     */
    @Transactional
    public CustomerResponseDto createCustomer(CustomerRequestDto requestDto) {
        log.info("Creating new customer with RUT: {}", requestDto.getRut());
        Customer customer = Customer.builder()
                .rut(requestDto.getRut())
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .status(true)
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        return mapToDto(savedCustomer);
    }

    /**
     * Obtiene todos los clientes del sistema.
     * @return Lista de clientes como DTOs de respuesta
     */
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un cliente por su ID.
     * @param id Identificador unico del cliente
     * @return DTO de respuesta con los datos del cliente
     * @throws CustomerNotFoundException si no se encuentra el cliente
     */
    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(Long id) {
        log.info("Fetching customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));
        return mapToDto(customer);
    }

    /**
     * Actualiza un cliente existente.
     * @param id Identificador del cliente a actualizar
     * @param requestDto DTO con los nuevos datos del cliente
     * @return DTO de respuesta con el cliente actualizado
     * @throws CustomerNotFoundException si no se encuentra el cliente
     */
    @Transactional
    public CustomerResponseDto updateCustomer(Long id, CustomerRequestDto requestDto) {
        log.info("Updating customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        customer.setRut(requestDto.getRut());
        customer.setName(requestDto.getName());
        customer.setEmail(requestDto.getEmail());

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToDto(updatedCustomer);
    }

    /**
     * Desactiva un cliente del sistema (soft delete).
     * @param id Identificador del cliente a desactivar
     * @throws CustomerNotFoundException si no se encuentra el cliente
     */
    @Transactional
    public void deactivateCustomer(Long id) {
        log.info("Deactivating customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));

        customer.setStatus(false);
        customerRepository.save(customer);
    }

    private CustomerResponseDto mapToDto(Customer customer) {
        return CustomerResponseDto.builder()
                .id(customer.getId())
                .rut(customer.getRut())
                .name(customer.getName())
                .email(customer.getEmail())
                .status(customer.getStatus())
                .build();
    }
}
