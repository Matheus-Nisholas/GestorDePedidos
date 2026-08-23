package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.Mapper.OrderMapper;
import com.nisholas.ordermanagement.entity.Customer;
import com.nisholas.ordermanagement.entity.Order;
import com.nisholas.ordermanagement.entity.OrderItem;
import com.nisholas.ordermanagement.entity.OrderStatus;
import com.nisholas.ordermanagement.entity.Product;
import com.nisholas.ordermanagement.exception.InvalidOrderStatusException;
import com.nisholas.ordermanagement.exception.ResourceNotFoundException;
import com.nisholas.ordermanagement.repository.CustomerRepository;
import com.nisholas.ordermanagement.repository.OrderItemRepository;
import com.nisholas.ordermanagement.repository.OrderRepository;
import com.nisholas.ordermanagement.repository.ProductRepository;
import com.nisholas.ordermanagement.request.OrderPatchRequest;
import com.nisholas.ordermanagement.request.OrderRequest;
import com.nisholas.ordermanagement.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order saveOrder(OrderRequest orderRequest) {
        if (orderRequest.status() != OrderStatus.CREATED) {
            throw new InvalidOrderStatusException(
                    "New orders must start with CREATED status"
            );
        }

        Customer customer = customerRepository.findById(orderRequest.customerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + orderRequest.customerId()
                ));

        Order order = OrderMapper.toOrder(orderRequest);
        order.setCustomer(customer);

        return orderRepository.save(order);
    }

    public OrderResponse getByOrderId(Long id) {
        Order order = findOrder(id);
        return OrderMapper.toOrderResponse(order);
    }

    public OrderResponse deleteByOrderId(Long id) {
        Order order = findOrder(id);

        if (!orderItemRepository.findByOrderId(id).isEmpty()) {
            throw new InvalidOrderStatusException(
                    "Order with items cannot be deleted. Cancel the order instead"
            );
        }

        orderRepository.deleteById(id);
        return OrderMapper.toOrderResponse(order);
    }

    @Transactional
    public Order putByOrderId(Long id, OrderRequest orderRequest) {
        Order existingOrder = findOrder(id);
        validateOrderEditable(existingOrder);

        Customer customer = customerRepository.findById(orderRequest.customerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + orderRequest.customerId()
                ));

        changeStatus(existingOrder, orderRequest.status());
        existingOrder.setCustomer(customer);

        return orderRepository.save(existingOrder);
    }

    @Transactional
    public Order patchOrder(Long id, OrderPatchRequest request) {
        Order existingOrder = findOrder(id);
        validateOrderEditable(existingOrder);

        if (request.customerId() != null) {
            Customer customer = customerRepository.findById(request.customerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Customer not found with id: " + request.customerId()
                    ));
            existingOrder.setCustomer(customer);
        }

        if (request.status() != null) {
            changeStatus(existingOrder, request.status());
        }

        return orderRepository.save(existingOrder);
    }

    private Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));
    }

    private void validateOrderEditable(Order order) {
        if (order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusException(
                    "Order with status " + order.getStatus() + " cannot be modified"
            );
        }
    }

    private void changeStatus(Order order, OrderStatus newStatus) {
        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == newStatus) {
            return;
        }

        boolean validTransition = switch (currentStatus) {
            case CREATED -> newStatus == OrderStatus.CONFIRMED ||
                    newStatus == OrderStatus.CANCELLED;
            case CONFIRMED -> newStatus == OrderStatus.SHIPPED ||
                    newStatus == OrderStatus.CANCELLED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!validTransition) {
            throw new InvalidOrderStatusException(
                    "Invalid order status transition: " + currentStatus + " -> " + newStatus
            );
        }

        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(newStatus);
    }

    private void restoreStock(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            product.setStockQuantity(
                    product.getStockQuantity() + orderItem.getQuantity()
            );
            productRepository.save(product);
        }
    }
}
