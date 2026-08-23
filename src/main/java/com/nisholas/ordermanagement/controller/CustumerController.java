package com.nisholas.ordermanagement.controller;


import com.nisholas.ordermanagement.Mapper.CustomerMapper;
import com.nisholas.ordermanagement.entity.Customer;
import com.nisholas.ordermanagement.request.CustomerRequest;
import com.nisholas.ordermanagement.response.CustomerResponse;
import com.nisholas.ordermanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustumerController {

    private final CustomerService customerService;

    @GetMapping()
    public ResponseEntity<List<CustomerResponse>> getAllCustumers() {
        List<Customer> customers = customerService.findAll();
        List<CustomerResponse> list = customers.stream().map(customer -> CustomerMapper.toCustomerResponse(customer)).toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> saveCustomer(@RequestBody CustomerRequest request) {
        Customer newCustomer = CustomerMapper.toCustomer(request);
        Customer savedCustomer = customerService.saveCustomer(newCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerMapper.toCustomerResponse(savedCustomer));

    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getByCustomerId(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getByCustomerId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomerResponse> deleteByCustomerId(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(customerService.deleteByCustomerId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> putByCustomerId(@PathVariable Long id, @RequestBody CustomerRequest request) {
        Customer customer = customerService.putByCustomerId(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(CustomerMapper.toCustomerResponse(customer));
    }
}
