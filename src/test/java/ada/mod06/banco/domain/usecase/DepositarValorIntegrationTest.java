package ada.mod06.banco.domain.usecase;

import ada.mod06.banco.domain.model.Conta;
import ada.mod06.banco.domain.model.Transacao;
import ada.mod06.banco.domain.model.enums.ContaTipo;
import ada.mod06.banco.domain.model.enums.TransacaoTipo;
import ada.mod06.banco.infra.gateway.bd.ContaRepository;
import ada.mod06.banco.infra.gateway.bd.TransacaoRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DepositarValorIntegrationTest {
    private Conta novaConta;

    private Transacao novaTransacao;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CriarNovaConta criarNovaConta;

    @Autowired
    private DepositarValor depositarValor;

    @BeforeEach
    void beforeEach() throws Exception {
        /*contaRepository.deleteAll();
        transacaoRepository.deleteAll();*/

        novaConta = new Conta(
                1L,
                2L,
                3L,
                BigDecimal.ZERO,
                ContaTipo.POUPANCA,
                "Ligia",
                "12312445212"
        );

        criarNovaConta.execute(novaConta);
    }

    /*@AfterEach
    void afterEach() {
        transacaoRepository.deleteAll();
        contaRepository.deleteAll();
    }*/

    @Test
    public void deveDepositarComSucesso() throws Exception {
        // Given

        // When
        Conta contaAtualizada = depositarValor.execute(novaConta, BigDecimal.valueOf(100.15));

        novaTransacao = transacaoRepository.findById(1L).get();

        // Then
        Assertions.assertAll(
                "Testes para a conta",
                () -> Assertions.assertEquals(1L, contaAtualizada.getId()),
                () -> Assertions.assertEquals(2L, contaAtualizada.getAgencia()),
                () -> Assertions.assertEquals(3L, contaAtualizada.getDigito()),
                () -> Assertions.assertEquals(BigDecimal.valueOf(100.15), contaAtualizada.getSaldo()),
                () -> Assertions.assertEquals("Ligia", contaAtualizada.getTitular()),
                () -> Assertions.assertEquals("12312445212", contaAtualizada.getCpf())
        );

        Assertions.assertAll(
                "Testes para a transação",
                () -> Assertions.assertNotNull(novaTransacao),
                () -> Assertions.assertEquals(1L, novaTransacao.getId()),
                () -> Assertions.assertEquals(contaAtualizada, novaTransacao.getConta()),
                () -> Assertions.assertEquals(contaAtualizada.getId(), novaTransacao.getConta().getId()),
                () -> Assertions.assertEquals(TransacaoTipo.DEPOSITO, novaTransacao.getTipo())
        );
    }

    @Test
    public void deveLancarUmaExceptionCasoUsuarioForneceUmaContaInvalida() {
        // Given

        // When
        novaConta.setCpf("123");

        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> depositarValor.execute(novaConta, BigDecimal.valueOf(2000.00))
        );

        // Then
        Assertions.assertEquals(
                "A conta informada é inválida!",
                throwable.getLocalizedMessage()
        );
    }
}
