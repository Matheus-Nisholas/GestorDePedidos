package com.nisholas.ordermanagement;

import com.jayway.jsonpath.JsonPath;
import com.nisholas.ordermanagement.entity.OrderStatus;
import com.nisholas.ordermanagement.entity.Role;
import com.nisholas.ordermanagement.entity.User;
import com.nisholas.ordermanagement.repository.CustomerRepository;
import com.nisholas.ordermanagement.repository.OrderItemRepository;
import com.nisholas.ordermanagement.repository.OrderRepository;
import com.nisholas.ordermanagement.repository.ProductRepository;
import com.nisholas.ordermanagement.repository.UserRepository;
import com.nisholas.ordermanagement.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class EndToEndFlowTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldCompleteMainOrderFlow() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@email.com",
                                  "password": "12345678"
                                }
                                """))
                .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@email.com",
                                  "password": "12345678"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        String userToken = JsonPath.read(
                loginResult.getResponse().getContentAsString(),
                "$.token"
        );

        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson()))
                .andExpect(status().isForbidden());

        User admin = userRepository.save(User.builder()
                .email("admin@email.com")
                .password(passwordEncoder.encode("12345678"))
                .role(Role.ADMIN)
                .active(true)
                .build());

        String adminToken = jwtService.generateToken(admin);

        MvcResult productResult = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson()))
                .andExpect(status().isCreated())
                .andReturn();

        Integer productIdValue = JsonPath.read(
                productResult.getResponse().getContentAsString(),
                "$.id"
        );
        Long productId = productIdValue.longValue();

        MvcResult customerResult = mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Cliente Teste",
                                  "email": "cliente@email.com",
                                  "phone": "21999999999",
                                  "active": true
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        Integer customerIdValue = JsonPath.read(
                customerResult.getResponse().getContentAsString(),
                "$.id"
        );
        Long customerId = customerIdValue.longValue();

        MvcResult orderResult = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": %d,
                                  "status": "CREATED"
                                }
                                """.formatted(customerId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andReturn();

        Integer orderIdValue = JsonPath.read(
                orderResult.getResponse().getContentAsString(),
                "$.id"
        );
        Long orderId = orderIdValue.longValue();

        mockMvc.perform(post("/order-items")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "orderId": %d,
                                  "productId": %d,
                                  "quantity": 2
                                }
                                """.formatted(orderId, productId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(2));

        assertEquals(
                8,
                productRepository.findById(productId).orElseThrow().getStockQuantity()
        );
        assertEquals(
                0,
                new BigDecimal("199.80").compareTo(
                        orderRepository.findById(orderId).orElseThrow().getTotalAmount()
                )
        );

        mockMvc.perform(patch("/orders/{id}", orderId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "CANCELLED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        assertEquals(
                10,
                productRepository.findById(productId).orElseThrow().getStockQuantity()
        );
        assertEquals(
                OrderStatus.CANCELLED,
                orderRepository.findById(orderId).orElseThrow().getStatus()
        );

        mockMvc.perform(get("/customers/999999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": %d,
                                  "status": "DELIVERED"
                                }
                                """.formatted(customerId)))
                .andExpect(status().isConflict());
    }

    private String productJson() {
        return """
                {
                  "name": "Mouse Gamer",
                  "description": "Produto usado no teste ponta a ponta",
                  "price": 99.90,
                  "stockQuantity": 10,
                  "active": true
                }
                """;
    }
}
