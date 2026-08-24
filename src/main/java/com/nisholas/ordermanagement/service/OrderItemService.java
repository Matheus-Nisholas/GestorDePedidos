package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.Mapper.OrderItemMapper;
import com.nisholas.ordermanagement.entity.Order;
import com.nisholas.ordermanagement.entity.OrderItem;
import com.nisholas.ordermanagement.entity.OrderStatus;
import com.nisholas.ordermanagement.entity.Product;
import com.nisholas.ordermanagement.exception.InsufficientStockException;
import com.nisholas.ordermanagement.exception.InvalidOrderStatusException;
import com.nisholas.ordermanagement.exception.ProductUnavailableException;
import com.nisholas.ordermanagement.exception.ResourceNotFoundException;
import com.nisholas.ordermanagement.repository.OrderItemRepository;
import com.nisholas.ordermanagement.repository.OrderRepository;
import com.nisholas.ordermanagement.repository.ProductRepository;
import com.nisholas.ordermanagement.request.OrderItemPatchRequest;
import com.nisholas.ordermanagement.request.OrderItemRequest;
import com.nisholas.ordermanagement.response.OrderItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<OrderItem> findAll() {
        return orderItemRepository.findAll();
    }

    public OrderItemResponse getByOrderItemId(Long id) {
        return OrderItemMapper.toOrderItemResponse(findOrderItem(id));
    }

    @Transactional
    public OrderItem saveOrderItem(OrderItemRequest request) {
        Order order = findOrder(request.orderId());
        validateOrderAllowsItemChanges(order);

        Product product = findProduct(request.productId());
        validateProductAvailable(product);
        validateStock(product, request.quantity());

        BigDecimal unitPrice = product.getPrice();
        BigDecimal subtotal = calculateSubtotal(unitPrice, request.quantity());

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(request.quantity())
                .unitPrice(unitPrice)
                .subtotal(subtotal)
                .build();

        product.setStockQuantity(product.getStockQuantity() - request.quantity());
        order.setTotalAmount(order.getTotalAmount().add(subtotal));

        productRepository.save(product);
        orderRepository.save(order);

        return orderItemRepository.save(orderItem);
    }

    @Transactional
    public OrderItem putByOrderItemId(Long id, OrderItemRequest request) {
        OrderItem existingItem = findOrderItem(id);
        validateOrderAllowsItemChanges(existingItem.getOrder());

        Order newOrder = findOrder(request.orderId());
        validateOrderAllowsItemChanges(newOrder);

        restoreOldItem(existingItem);

        Product newProduct = findProduct(request.productId());
        validateProductAvailable(newProduct);
        validateStock(newProduct, request.quantity());

        BigDecimal unitPrice = newProduct.getPrice();
        BigDecimal subtotal = calculateSubtotal(unitPrice, request.quantity());

        newProduct.setStockQuantity(newProduct.getStockQuantity() - request.quantity());
        newOrder.setTotalAmount(newOrder.getTotalAmount().add(subtotal));

        existingItem.setOrder(newOrder);
        existingItem.setProduct(newProduct);
        existingItem.setQuantity(request.quantity());
        existingItem.setUnitPrice(unitPrice);
        existingItem.setSubtotal(subtotal);

        productRepository.save(newProduct);
        orderRepository.save(newOrder);

        return orderItemRepository.save(existingItem);
    }

    @Transactional
    public OrderItem patchOrderItem(Long id, OrderItemPatchRequest request) {
        OrderItem existingItem = findOrderItem(id);
        validateOrderAllowsItemChanges(existingItem.getOrder());

        Long orderId = request.orderId() != null
                ? request.orderId()
                : existingItem.getOrder().getId();

        Long productId = request.productId() != null
                ? request.productId()
                : existingItem.getProduct().getId();

        int quantity = request.quantity() != null
                ? request.quantity()
                : existingItem.getQuantity();

        Order newOrder = findOrder(orderId);
        validateOrderAllowsItemChanges(newOrder);

        restoreOldItem(existingItem);

        Product newProduct = findProduct(productId);
        validateProductAvailable(newProduct);
        validateStock(newProduct, quantity);

        BigDecimal unitPrice = newProduct.getPrice();
        BigDecimal subtotal = calculateSubtotal(unitPrice, quantity);

        newProduct.setStockQuantity(newProduct.getStockQuantity() - quantity);
        newOrder.setTotalAmount(newOrder.getTotalAmount().add(subtotal));

        existingItem.setOrder(newOrder);
        existingItem.setProduct(newProduct);
        existingItem.setQuantity(quantity);
        existingItem.setUnitPrice(unitPrice);
        existingItem.setSubtotal(subtotal);

        productRepository.save(newProduct);
        orderRepository.save(newOrder);

        return orderItemRepository.save(existingItem);
    }

    @Transactional
    public OrderItemResponse deleteByOrderItemId(Long id) {
        OrderItem orderItem = findOrderItem(id);
        validateOrderAllowsItemChanges(orderItem.getOrder());

        restoreOldItem(orderItem);
        orderItemRepository.delete(orderItem);

        return OrderItemMapper.toOrderItemResponse(orderItem);
    }

    private OrderItem findOrderItem(Long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order item not found with id: " + id
                ));
    }

    private Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + id
                ));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));
    }

    private void validateOrderAllowsItemChanges(Order order) {
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new InvalidOrderStatusException(
                    "Order items can only be modified while order status is CREATED"
            );
        }
    }

    private void validateProductAvailable(Product product) {
        if (!product.isActive()) {
            throw new ProductUnavailableException(
                    "Product is inactive and cannot be added to an order. Product id: " + product.getId()
            );
        }
    }

    private void validateStock(Product product, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (product.getStockQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for product id: " + product.getId()
            );
        }
    }

    private BigDecimal calculateSubtotal(BigDecimal unitPrice, int quantity) {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    private void restoreOldItem(OrderItem orderItem) {
        Product oldProduct = orderItem.getProduct();
        Order oldOrder = orderItem.getOrder();

        oldProduct.setStockQuantity(
                oldProduct.getStockQuantity() + orderItem.getQuantity()
        );

        oldOrder.setTotalAmount(
                oldOrder.getTotalAmount().subtract(orderItem.getSubtotal())
        );

        productRepository.save(oldProduct);
        orderRepository.save(oldOrder);
    }
}
