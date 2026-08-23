package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.entity.Customer;
import com.nisholas.ordermanagement.exception.EmailAlreadyExistsException;
import com.nisholas.ordermanagement.exception.ResourceNotFoundException;
import com.nisholas.ordermanagement.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldSaveCustomerWhenEmailDoesNotExist() {
        Customer customer = Customer.builder()
                .id(1L)
                .name("Matheus")
                .email("matheus@email.com")
                .active(true)
                .build();

        when(customerRepository.existsByEmail(customer.getEmail())).thenReturn(false);
        when(customerRepository.save(customer)).thenReturn(customer);

        Customer savedCustomer = customerService.saveCustomer(customer);

        assertEquals(1L, savedCustomer.getId());
        assertEquals("Matheus", savedCustomer.getName());
        verify(customerRepository).save(customer);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        Customer customer = Customer.builder()
                .email("duplicado@email.com")
                .build();

        when(customerRepository.existsByEmail(customer.getEmail())).thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> customerService.saveCustomer(customer)
        );

        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void shouldThrowNotFoundWhenCustomerDoesNotExist() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getByCustomerId(99L)
        );
    }
}
