package com.nisholas.ordermanagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestão de Pedidos")
                        .description("API REST para gerenciamento de clientes, produtos, pedidos e itens de pedido, com controle de estoque e regras de status do pedido.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Matheus Nísholas")));
    }
}
