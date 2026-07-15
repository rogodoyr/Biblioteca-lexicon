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

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

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

    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto getCustomerById(Long id) {
        log.info("Fetching customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + id));
        return mapToDto(customer);
    }

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
