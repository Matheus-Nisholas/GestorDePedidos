package com.nisholas.ordermanagement.Mapper;

import com.nisholas.ordermanagement.entity.Product;
import com.nisholas.ordermanagement.request.ProductRequest;
import com.nisholas.ordermanagement.response.ProductResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductMapper {

    public static Product toProduct(ProductRequest productRequest) {
        return Product
                .builder()
                .name(productRequest.name())
                .build();
    }

    public static ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .build();
    }
}
