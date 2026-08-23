package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.entity.Product;
import com.nisholas.ordermanagement.exception.ResourceNotFoundException;
import com.nisholas.ordermanagement.repository.ProductRepository;
import com.nisholas.ordermanagement.request.ProductRequest;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldSaveProduct() {
        ProductRequest request = new ProductRequest(
                "Teclado",
                "Teclado mecânico",
                new BigDecimal("329.90"),
                10,
                true
        );

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product savedProduct = productService.saveProduct(request);

        assertEquals("Teclado", savedProduct.getName());
        assertEquals(new BigDecimal("329.90"), savedProduct.getPrice());
        assertEquals(10, savedProduct.getStockQuantity());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldReturnProductById() {
        Product product = Product.builder()
                .id(1L)
                .name("Mouse")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var response = productService.getByProductId(1L);

        assertEquals(1L, response.id());
        assertEquals("Mouse", response.name());
    }

    @Test
    void shouldThrowNotFoundWhenProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getByProductId(99L)
        );
    }
}
