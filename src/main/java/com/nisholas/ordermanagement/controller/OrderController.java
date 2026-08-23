package com.nisholas.ordermanagement.controller;

import com.nisholas.ordermanagement.Mapper.OrderMapper;
import com.nisholas.ordermanagement.entity.Order;
import com.nisholas.ordermanagement.request.OrderPatchRequest;
import com.nisholas.ordermanagement.request.OrderRequest;
import com.nisholas.ordermanagement.response.OrderResponse;
import com.nisholas.ordermanagement.service.OrderService;
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
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Operações de criação, consulta e alteração do ciclo de vida dos pedidos")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Retorna todos os pedidos cadastrados.")
    @ApiResponse(responseCode = "200", description = "Pedidos listados com sucesso")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.findAll()
                .stream()
                .map(OrderMapper::toOrderResponse)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    @Operation(summary = "Criar pedido", description = "Cria um pedido associado a um cliente. Novos pedidos devem iniciar no status CREATED.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados do pedido inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "Status inicial do pedido inválido")
    })
    public ResponseEntity<OrderResponse> saveOrder(@Valid @RequestBody OrderRequest request) {
        Order savedOrder = orderService.saveOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderMapper.toOrderResponse(savedOrder));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido pelo identificador informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<OrderResponse> getByOrderId(
            @Parameter(description = "ID do pedido", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getByOrderId(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pedido", description = "Remove um pedido sem itens. Pedidos com itens devem ser cancelados para restaurar o estoque.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "409", description = "Pedido não pode ser excluído no estado atual")
    })
    public ResponseEntity<OrderResponse> deleteByOrderId(
            @Parameter(description = "ID do pedido", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.deleteByOrderId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido", description = "Atualiza cliente e status do pedido respeitando as transições de status permitidas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Pedido ou cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "Transição de status inválida")
    })
    public ResponseEntity<OrderResponse> putByOrderId(
            @Parameter(description = "ID do pedido", example = "1") @PathVariable Long id,
            @Valid @RequestBody OrderRequest request) {
        Order order = orderService.putByOrderId(id, request);
        return ResponseEntity.ok(OrderMapper.toOrderResponse(order));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar parcialmente pedido", description = "Altera somente os campos enviados. Mudanças de status respeitam o fluxo CREATED → CONFIRMED → SHIPPED → DELIVERED, com cancelamento quando permitido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido ou cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "Transição de status inválida")
    })
    public ResponseEntity<OrderResponse> patchOrder(
            @Parameter(description = "ID do pedido", example = "1") @PathVariable Long id,
            @Valid @RequestBody OrderPatchRequest request) {
        Order order = orderService.patchOrder(id, request);
        return ResponseEntity.ok(OrderMapper.toOrderResponse(order));
    }
}
