package ada.mod06.banco.domain.usecase;

import ada.mod06.banco.domain.model.Conta;
import ada.mod06.banco.domain.model.Transacao;
import ada.mod06.banco.domain.model.enums.ContaTipo;
import ada.mod06.banco.domain.model.enums.TransacaoTipo;
import ada.mod06.banco.infra.gateway.bd.TransacaoRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SacarValorIntegrationTest {
    private Conta novaConta;

    private Transacao novaTransacao;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CriarNovaConta criarNovaConta;

    @Autowired
    private SacarValor sacarValor;

    @BeforeEach
    void beforeEach() throws Exception {
        novaConta = new Conta(
                1L,
                2L,
                3L,
                BigDecimal.valueOf(1000),
                ContaTipo.POUPANCA,
                "Ligia",
                "12312445212"
        );

        criarNovaConta.execute(novaConta);
    }

    @Test
    public void deveSacarComSucesso() throws Exception {
        // Given

        // When
        Conta contaAtualizada = sacarValor.execute(novaConta, BigDecimal.valueOf(100.15));

        novaTransacao = transacaoRepository.findById(1L).get();

        // Then
        Assertions.assertAll(
                "Testes para a conta",
                () -> Assertions.assertEquals(1L, contaAtualizada.getId()),
                () -> Assertions.assertEquals(2L, contaAtualizada.getAgencia()),
                () -> Assertions.assertEquals(3L, contaAtualizada.getDigito()),
                () -> Assertions.assertEquals(BigDecimal.valueOf(899.85), contaAtualizada.getSaldo()),
                () -> Assertions.assertEquals("Ligia", contaAtualizada.getTitular()),
                () -> Assertions.assertEquals("12312445212", contaAtualizada.getCpf())
        );
        Assertions.assertAll(
                "Testando informações da transação",
                () -> Assertions.assertNotNull(novaTransacao),
                () -> Assertions.assertEquals(1L, novaTransacao.getId()),
                () -> Assertions.assertEquals(contaAtualizada, novaTransacao.getConta()),
                () -> Assertions.assertEquals(contaAtualizada.getId(), novaTransacao.getConta().getId()),
                () -> Assertions.assertEquals(TransacaoTipo.SAQUE, novaTransacao.getTipo())
        );
    }

    @Test
    public void deveLancarUmaExceptionCasoUsuarioForneceUmaContaInvalida() {
        // Given

        // When
        novaConta.setCpf("123");

        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> sacarValor.execute(novaConta, BigDecimal.valueOf(2000.00))
        );

        // Then
        Assertions.assertEquals(
                "A conta informada é inválida!",
                throwable.getLocalizedMessage()
        );
    }

    @Test
    public void deveLancarUmaExceptionCasoUsuarioForneceUmValorMaiorQueSaldo() throws Exception {
        // Given

        // When
        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> sacarValor.execute(novaConta, BigDecimal.valueOf(2100.00))
        );

        // Then
        Assertions.assertEquals(
                "Não é possível sacar valor informado!",
                throwable.getLocalizedMessage()
        );
    }
}
