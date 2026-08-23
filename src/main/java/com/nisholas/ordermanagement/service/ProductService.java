package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.Mapper.ProductMapper;
import com.nisholas.ordermanagement.entity.Product;
import com.nisholas.ordermanagement.exception.ResourceNotFoundException;
import com.nisholas.ordermanagement.repository.ProductRepository;
import com.nisholas.ordermanagement.request.ProductPatchRequest;
import com.nisholas.ordermanagement.request.ProductRequest;
import com.nisholas.ordermanagement.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product saveProduct(ProductRequest productRequest) {
        Product product = ProductMapper.toProduct(productRequest);
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());
        product.setStockQuantity(productRequest.stockQuantity());
        product.setActive(productRequest.active());

        return productRepository.save(product);
    }

    public ProductResponse getByProductId(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        return ProductMapper.toProductResponse(product);
    }

    public ProductResponse deleteByProductId(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        productRepository.deleteById(id);
        return ProductMapper.toProductResponse(product);
    }

    public Product putByProductId(Long id, ProductRequest productRequest) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        existingProduct.setName(productRequest.name());
        existingProduct.setDescription(productRequest.description());
        existingProduct.setPrice(productRequest.price());
        existingProduct.setStockQuantity(productRequest.stockQuantity());
        existingProduct.setActive(productRequest.active());

        return productRepository.save(existingProduct);
    }

    public Product patchProduct(Long id, ProductPatchRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id
                ));

        if (request.name() != null) {
            existingProduct.setName(request.name());
        }

        if (request.description() != null) {
            existingProduct.setDescription(request.description());
        }

        if (request.price() != null) {
            existingProduct.setPrice(request.price());
        }

        if (request.stockQuantity() != null) {
            existingProduct.setStockQuantity(request.stockQuantity());
        }

        if (request.active() != null) {
            existingProduct.setActive(request.active());
        }

        return productRepository.save(existingProduct);
    }
}
