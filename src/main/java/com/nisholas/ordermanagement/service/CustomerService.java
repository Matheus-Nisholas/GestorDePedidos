package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.Mapper.CustomerMapper;
import com.nisholas.ordermanagement.entity.Customer;
import com.nisholas.ordermanagement.repository.CustomerRepository;
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
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            return CustomerMapper.toCustomerResponse(optionalCustomer.get());
        } else {
            return null;
        }
    }

    public CustomerResponse deleteByCustomerId(Long id){
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()){
            customerRepository.deleteById(id);
            return CustomerMapper.toCustomerResponse(optionalCustomer.get());
        } else {
            return null;
        }
    }
}
