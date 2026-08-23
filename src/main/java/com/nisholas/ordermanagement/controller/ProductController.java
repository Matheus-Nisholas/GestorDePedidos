package com.nisholas.ordermanagement.controller;

import com.nisholas.ordermanagement.Mapper.ProductMapper;
import com.nisholas.ordermanagement.entity.Product;
import com.nisholas.ordermanagement.request.ProductPatchRequest;
import com.nisholas.ordermanagement.request.ProductRequest;
import com.nisholas.ordermanagement.response.ProductResponse;
import com.nisholas.ordermanagement.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Operações de cadastro e gerenciamento de produtos e estoque")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Listar produtos", description = "Retorna todos os produtos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Produtos listados com sucesso")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.findAll()
                .stream()
                .map(ProductMapper::toProductResponse)
                .toList();
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @Operation(summary = "Cadastrar produto", description = "Cria um novo produto com preço, estoque e status de ativação.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados do produto inválidos")
    })
    public ResponseEntity<ProductResponse> saveProduct(@Valid @RequestBody ProductRequest request) {
        Product savedProduct = productService.saveProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductMapper.toProductResponse(savedProduct));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto pelo identificador informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProductResponse> getByProductId(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(productService.getByProductId(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir produto", description = "Remove um produto pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProductResponse> deleteByProductId(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteByProductId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto", description = "Substitui os dados editáveis de um produto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProductResponse> putByProductId(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        Product product = productService.putByProductId(id, request);
        return ResponseEntity.ok(ProductMapper.toProductResponse(product));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar parcialmente produto", description = "Altera somente os campos enviados no corpo da requisição.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    public ResponseEntity<ProductResponse> patchProduct(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProductPatchRequest request) {
        Product product = productService.patchProduct(id, request);
        return ResponseEntity.ok(ProductMapper.toProductResponse(product));
    }
}
