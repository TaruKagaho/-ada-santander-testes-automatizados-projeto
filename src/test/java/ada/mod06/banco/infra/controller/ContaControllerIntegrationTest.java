package ada.mod06.banco.infra.controller;

import ada.mod06.banco.domain.model.Conta;
import ada.mod06.banco.domain.model.enums.ContaTipo;
import ada.mod06.banco.infra.dto.DepositoDto;
import ada.mod06.banco.infra.gateway.bd.ContaRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ContaControllerIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ContaController contaController;

    @BeforeEach
    void beforeEach() {
        contaRepository.deleteAll();
    }

    @Test
    void criarConta_ComSucesso_DeveRetornarStatus201() throws Exception {
        // Arrange
        String requestBody = objectMapper.writeValueAsString(
                new Conta(
                        1L,
                        2L,
                        3L,
                        BigDecimal.ZERO,
                        ContaTipo.POUPANCA,
                        "Pedro",
                        "123456789"
                )
        );

        // Act
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Conta conta = contaRepository.findByCpf("123456789");

        // Assert
        Assertions.assertNotNull(conta);
    }

    @Test
    void criarConta_JaExistente_DeveRetornarStatusBadRequest() throws Exception {
        // Arrange
        Conta conta = new Conta(
                1L,
                2L,
                3L,
                BigDecimal.ZERO,
                ContaTipo.CORRENTE,
                "Pedro",
                "123456789"
        );
        String requestBody = objectMapper.writeValueAsString(conta);

        contaRepository.save(conta);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void criarConta_ComSucesso_DeveSalvarAConta() throws Exception {
        Conta conta = new Conta(
                1L,
                2L,
                3L,
                BigDecimal.ZERO,
                ContaTipo.POUPANCA,
                "Pedro",
                "123456789"
        );

        // when
        contaController.criarConta(conta);

        // then
        Conta contaCriada = contaRepository.findByCpf("123456789");

        Assertions.assertEquals("Pedro", contaCriada.getTitular());
    }

    @Test
    void depositarValorComSucesso() throws Exception {
        // Given
        Conta conta = contaRepository.save(
                new Conta(
                        2L,
                        3L,
                        BigDecimal.ZERO,
                        ContaTipo.POUPANCA,
                        "Pedro",
                        "123456789"
                )
        );
        String requestBody = objectMapper.writeValueAsString(
                new DepositoDto(
                        conta,
                        BigDecimal.valueOf(100.00)
                )
        );

        // When
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/contas/depositos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        Conta contaAtualizada = contaRepository.findByCpf("123456789");

        // Then
        Assertions.assertAll(
                "Teste de depósito",
                () -> Assertions.assertNotNull(contaAtualizada),
                () -> Assertions.assertEquals(BigDecimal.valueOf(100).setScale(2), contaAtualizada.getSaldo())
        );
    }
}
