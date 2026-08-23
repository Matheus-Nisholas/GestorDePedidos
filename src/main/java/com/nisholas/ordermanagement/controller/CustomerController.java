package com.nisholas.ordermanagement.controller;

import com.nisholas.ordermanagement.Mapper.CustomerMapper;
import com.nisholas.ordermanagement.entity.Customer;
import com.nisholas.ordermanagement.request.CustomerPatchRequest;
import com.nisholas.ordermanagement.request.CustomerRequest;
import com.nisholas.ordermanagement.response.CustomerResponse;
import com.nisholas.ordermanagement.service.CustomerService;
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
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Operações de cadastro e gerenciamento de clientes")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna todos os clientes cadastrados.")
    @ApiResponse(responseCode = "200", description = "Clientes listados com sucesso")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.findAll()
                .stream()
                .map(CustomerMapper::toCustomerResponse)
                .toList();
        return ResponseEntity.ok(customers);
    }

    @PostMapping
    @Operation(summary = "Cadastrar cliente", description = "Cria um novo cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados do cliente inválidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    public ResponseEntity<CustomerResponse> saveCustomer(@Valid @RequestBody CustomerRequest request) {
        Customer newCustomer = CustomerMapper.toCustomer(request);
        Customer savedCustomer = customerService.saveCustomer(newCustomer);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomerMapper.toCustomerResponse(savedCustomer));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID", description = "Retorna um cliente pelo identificador informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<CustomerResponse> getByCustomerId(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(customerService.getByCustomerId(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir cliente", description = "Remove um cliente pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<CustomerResponse> deleteByCustomerId(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(customerService.deleteByCustomerId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Substitui os dados editáveis de um cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "E-mail já pertence a outro cliente")
    })
    public ResponseEntity<CustomerResponse> putByCustomerId(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        Customer customer = customerService.putByCustomerId(id, request);
        return ResponseEntity.ok(CustomerMapper.toCustomerResponse(customer));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar parcialmente cliente", description = "Altera somente os campos enviados no corpo da requisição.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "409", description = "E-mail já pertence a outro cliente")
    })
    public ResponseEntity<CustomerResponse> patchCustomer(
            @Parameter(description = "ID do cliente", example = "1") @PathVariable Long id,
            @Valid @RequestBody CustomerPatchRequest request) {
        Customer customer = customerService.patchCustomer(id, request);
        return ResponseEntity.ok(CustomerMapper.toCustomerResponse(customer));
    }
}
