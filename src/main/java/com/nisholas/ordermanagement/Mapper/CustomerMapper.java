package com.nisholas.ordermanagement.Mapper;

import com.nisholas.ordermanagement.entity.Customer;
import com.nisholas.ordermanagement.request.CustomerRequest;
import com.nisholas.ordermanagement.response.CustomerResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CustomerMapper {

    public static Customer toCustomer(CustomerRequest customerRequest){
        return Customer
                .builder()
                .name(customerRequest.name())
                .build();
    }

    public static CustomerResponse toCustomerResponse (Customer customer){
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .build();
    }
}
