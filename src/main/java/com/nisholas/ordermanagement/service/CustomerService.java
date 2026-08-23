package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.Mapper.CustomerMapper;
import com.nisholas.ordermanagement.entity.Customer;
import com.nisholas.ordermanagement.exception.EmailAlreadyExistsException;
import com.nisholas.ordermanagement.exception.ResourceNotFoundException;
import com.nisholas.ordermanagement.repository.CustomerRepository;
import com.nisholas.ordermanagement.request.CustomerRequest;
import com.nisholas.ordermanagement.response.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer saveCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered: " + customer.getEmail()
            );
        } else {
            return customerRepository.save(customer);
        }
    }

    public CustomerResponse getByCustomerId(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: " + id
                        )
                );
        return CustomerMapper.toCustomerResponse(customer);
    }

    public CustomerResponse deleteByCustomerId(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: " + id
                        )
                );
        customerRepository.deleteById(id);
        return CustomerMapper.toCustomerResponse(customer);
    }

    public Customer putByCustomerId(Long id, CustomerRequest customerRequest) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + id
                ));

        if (customerRepository.existsByEmailAndIdNot(customerRequest.email(), id)) {
            throw new EmailAlreadyExistsException(
                    "Email already registered: " + customerRequest.email()
            );
        }
        existingCustomer.setName(customerRequest.name());
        existingCustomer.setEmail(customerRequest.email());
        existingCustomer.setPhone(customerRequest.phone());
        existingCustomer.setActive(customerRequest.active());

        return customerRepository.save(existingCustomer);

    }
}
