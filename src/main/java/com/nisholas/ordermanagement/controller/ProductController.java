package com.nisholas.ordermanagement.controller;

import com.nisholas.ordermanagement.Mapper.ProductMapper;
import com.nisholas.ordermanagement.entity.Product;
import com.nisholas.ordermanagement.request.ProductPatchRequest;
import com.nisholas.ordermanagement.request.ProductRequest;
import com.nisholas.ordermanagement.response.ProductResponse;
import com.nisholas.ordermanagement.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.findAll()
                .stream()
                .map(ProductMapper::toProductResponse)
                .toList();

        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> saveProduct(
            @Valid @RequestBody ProductRequest request) {

        Product savedProduct = productService.saveProduct(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ProductMapper.toProductResponse(savedProduct));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getByProductId(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getByProductId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductResponse> deleteByProductId(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteByProductId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> putByProductId(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        Product product = productService.putByProductId(id, request);

        return ResponseEntity.ok(ProductMapper.toProductResponse(product));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> patchProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductPatchRequest request) {

        Product product = productService.patchProduct(id, request);

        return ResponseEntity.ok(ProductMapper.toProductResponse(product));
    }
}
