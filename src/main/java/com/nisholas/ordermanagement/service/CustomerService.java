package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.Mapper.CustomerMapper;
import com.nisholas.ordermanagement.entity.Customer;
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
        return customerRepository.save(customer);
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
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            customerRepository.deleteById(id);
            return CustomerMapper.toCustomerResponse(optionalCustomer.get());
        } else {
            return null;
        }
    }

    public Customer putByCustomerId(Long id, CustomerRequest customerRequest) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow();

        existingCustomer.setName(customerRequest.name());
        existingCustomer.setEmail(customerRequest.email());
        existingCustomer.setPhone(customerRequest.phone());
        existingCustomer.setActive(customerRequest.active());

        return customerRepository.save(existingCustomer);

    }
}
