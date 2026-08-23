package com.nisholas.ordermanagement.controller;

import com.nisholas.ordermanagement.Mapper.OrderItemMapper;
import com.nisholas.ordermanagement.entity.OrderItem;
import com.nisholas.ordermanagement.request.OrderItemPatchRequest;
import com.nisholas.ordermanagement.request.OrderItemRequest;
import com.nisholas.ordermanagement.response.OrderItemResponse;
import com.nisholas.ordermanagement.service.OrderItemService;
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
@RequestMapping("/order-items")
@RequiredArgsConstructor
@Tag(name = "Itens de Pedido", description = "Operações dos itens dos pedidos, incluindo cálculo de subtotal e atualização de estoque")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    @Operation(summary = "Listar itens de pedido", description = "Retorna todos os itens de pedido cadastrados.")
    @ApiResponse(responseCode = "200", description = "Itens listados com sucesso")
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {
        List<OrderItemResponse> orderItems = orderItemService.findAll()
                .stream()
                .map(OrderItemMapper::toOrderItemResponse)
                .toList();
        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item por ID", description = "Retorna um item de pedido pelo identificador informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item encontrado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<OrderItemResponse> getByOrderItemId(
            @Parameter(description = "ID do item de pedido", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getByOrderItemId(id));
    }

    @PostMapping
    @Operation(summary = "Adicionar item ao pedido", description = "Adiciona um produto ao pedido, valida o estoque, calcula o subtotal, baixa o estoque e atualiza o valor total do pedido. Só é permitido em pedidos CREATED.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Pedido ou produto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Estoque insuficiente ou pedido não permite alterações")
    })
    public ResponseEntity<OrderItemResponse> saveOrderItem(
            @Valid @RequestBody OrderItemRequest request) {
        OrderItem orderItem = orderItemService.saveOrderItem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderItemMapper.toOrderItemResponse(orderItem));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item", description = "Substitui pedido, produto e quantidade do item. O estoque, subtotal e total do pedido são recalculados automaticamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Item, pedido ou produto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Estoque insuficiente ou pedido não permite alterações")
    })
    public ResponseEntity<OrderItemResponse> putByOrderItemId(
            @Parameter(description = "ID do item de pedido", example = "1") @PathVariable Long id,
            @Valid @RequestBody OrderItemRequest request) {
        OrderItem orderItem = orderItemService.putByOrderItemId(id, request);
        return ResponseEntity.ok(OrderItemMapper.toOrderItemResponse(orderItem));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar parcialmente item", description = "Altera somente os campos enviados e recalcula estoque, subtotal e total do pedido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Item, pedido ou produto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Estoque insuficiente ou pedido não permite alterações")
    })
    public ResponseEntity<OrderItemResponse> patchOrderItem(
            @Parameter(description = "ID do item de pedido", example = "1") @PathVariable Long id,
            @Valid @RequestBody OrderItemPatchRequest request) {
        OrderItem orderItem = orderItemService.patchOrderItem(id, request);
        return ResponseEntity.ok(OrderItemMapper.toOrderItemResponse(orderItem));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover item", description = "Remove o item, devolve sua quantidade ao estoque e recalcula o valor total do pedido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado"),
            @ApiResponse(responseCode = "409", description = "Pedido não permite alterações")
    })
    public ResponseEntity<OrderItemResponse> deleteByOrderItemId(
            @Parameter(description = "ID do item de pedido", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.deleteByOrderItemId(id));
    }
}
