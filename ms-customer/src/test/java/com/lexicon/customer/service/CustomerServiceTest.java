package com.lexicon.customer.service;

import com.lexicon.customer.dto.CustomerResponseDto;
import com.lexicon.customer.entity.Customer;
import com.lexicon.customer.exception.CustomerNotFoundException;
import com.lexicon.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getCustomerById_Success() {
        // Arrange
        Long customerId = 1L;
        Customer customer = Customer.builder()
                .id(customerId)
                .rut("12345678-9")
                .name("Test User")
                .email("test@example.com")
                .status(true)
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Act
        CustomerResponseDto response = customerService.getCustomerById(customerId);

        // Assert
        assertNotNull(response);
        assertEquals(customerId, response.getId());
        assertEquals("Test User", response.getName());
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    void getCustomerById_NotFound() {
        // Arrange
        Long customerId = 99L;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(customerId));
        verify(customerRepository, times(1)).findById(customerId);
    }
}
