package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.entity.Order;
import com.nisholas.ordermanagement.entity.OrderItem;
import com.nisholas.ordermanagement.entity.OrderStatus;
import com.nisholas.ordermanagement.entity.Product;
import com.nisholas.ordermanagement.exception.InsufficientStockException;
import com.nisholas.ordermanagement.repository.OrderItemRepository;
import com.nisholas.ordermanagement.repository.OrderRepository;
import com.nisholas.ordermanagement.repository.ProductRepository;
import com.nisholas.ordermanagement.request.OrderItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    void shouldAddItemAndUpdateStockAndOrderTotal() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.CREATED)
                .totalAmount(BigDecimal.ZERO)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Teclado")
                .price(new BigDecimal("329.90"))
                .stockQuantity(10)
                .active(true)
                .build();

        OrderItemRequest request = new OrderItemRequest(1L, 1L, 2);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderItemRepository.save(any(OrderItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderItem savedItem = orderItemService.saveOrderItem(request);

        assertEquals(2, savedItem.getQuantity());
        assertEquals(new BigDecimal("659.80"), savedItem.getSubtotal());
        assertEquals(8, product.getStockQuantity());
        assertEquals(new BigDecimal("659.80"), order.getTotalAmount());

        verify(productRepository).save(product);
        verify(orderRepository).save(order);
        verify(orderItemRepository).save(any(OrderItem.class));
    }

    @Test
    void shouldRejectItemWhenStockIsInsufficient() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.CREATED)
                .totalAmount(BigDecimal.ZERO)
                .build();

        Product product = Product.builder()
                .id(1L)
                .price(new BigDecimal("100.00"))
                .stockQuantity(1)
                .active(true)
                .build();

        OrderItemRequest request = new OrderItemRequest(1L, 1L, 5);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(
                InsufficientStockException.class,
                () -> orderItemService.saveOrderItem(request)
        );

        verify(orderItemRepository, never()).save(any(OrderItem.class));
        verify(productRepository, never()).save(any(Product.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
