package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.entity.Customer;
import com.nisholas.ordermanagement.entity.Order;
import com.nisholas.ordermanagement.entity.OrderStatus;
import com.nisholas.ordermanagement.exception.InvalidOrderStatusException;
import com.nisholas.ordermanagement.repository.CustomerRepository;
import com.nisholas.ordermanagement.repository.OrderItemRepository;
import com.nisholas.ordermanagement.repository.OrderRepository;
import com.nisholas.ordermanagement.repository.ProductRepository;
import com.nisholas.ordermanagement.request.OrderRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrderWithCreatedStatus() {
        Customer customer = Customer.builder()
                .id(4L)
                .name("Matheus")
                .build();

        OrderRequest request = new OrderRequest(4L, OrderStatus.CREATED);

        when(customerRepository.findById(4L)).thenReturn(Optional.of(customer));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order savedOrder = orderService.saveOrder(request);

        assertEquals(OrderStatus.CREATED, savedOrder.getStatus());
        assertEquals(4L, savedOrder.getCustomer().getId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldRejectNewOrderWithInvalidInitialStatus() {
        OrderRequest request = new OrderRequest(4L, OrderStatus.CONFIRMED);

        assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.saveOrder(request)
        );

        verify(customerRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldRejectInvalidStatusTransition() {
        Customer customer = Customer.builder().id(4L).build();
        Order existingOrder = Order.builder()
                .id(1L)
                .customer(customer)
                .status(OrderStatus.CREATED)
                .build();

        OrderRequest request = new OrderRequest(4L, OrderStatus.DELIVERED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existingOrder));
        when(customerRepository.findById(4L)).thenReturn(Optional.of(customer));

        assertThrows(
                InvalidOrderStatusException.class,
                () -> orderService.putByOrderId(1L, request)
        );

        verify(orderRepository, never()).save(existingOrder);
    }
}
