package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.Mapper.OrderMapper;
import com.nisholas.ordermanagement.entity.Customer;
import com.nisholas.ordermanagement.entity.Order;
import com.nisholas.ordermanagement.exception.ResourceNotFoundException;
import com.nisholas.ordermanagement.repository.CustomerRepository;
import com.nisholas.ordermanagement.repository.OrderRepository;
import com.nisholas.ordermanagement.request.OrderPatchRequest;
import com.nisholas.ordermanagement.request.OrderRequest;
import com.nisholas.ordermanagement.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order saveOrder(OrderRequest orderRequest) {
        Customer customer = customerRepository.findById(orderRequest.customerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + orderRequest.customerId()
                ));

        Order order = OrderMapper.toOrder(orderRequest);
        order.setCustomer(customer);

        return orderRepository.save(order);
    }

    public OrderResponse getByOrderId(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));

        return OrderMapper.toOrderResponse(order);
    }

    public OrderResponse deleteByOrderId(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));

        orderRepository.deleteById(id);
        return OrderMapper.toOrderResponse(order);
    }

    public Order putByOrderId(Long id, OrderRequest orderRequest) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));

        Customer customer = customerRepository.findById(orderRequest.customerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + orderRequest.customerId()
                ));

        existingOrder.setCustomer(customer);
        existingOrder.setStatus(orderRequest.status());

        return orderRepository.save(existingOrder);
    }

    public Order patchOrder(Long id, OrderPatchRequest request) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));

        if (request.customerId() != null) {
            Customer customer = customerRepository.findById(request.customerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Customer not found with id: " + request.customerId()
                    ));
            existingOrder.setCustomer(customer);
        }

        if (request.status() != null) {
            existingOrder.setStatus(request.status());
        }

        return orderRepository.save(existingOrder);
    }
}
