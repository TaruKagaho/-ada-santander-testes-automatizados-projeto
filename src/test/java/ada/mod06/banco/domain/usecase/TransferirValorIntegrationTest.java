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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransferirValorIntegrationTest {
    private Conta contaOrigem;

    private Conta contaDestino;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CriarNovaConta criarNovaConta;

    @Autowired
    private TransferirValor transferirValor;

    @BeforeEach
    void beforeEach() throws Exception {
        contaOrigem = new Conta(
                1L,
                2L,
                3L,
                BigDecimal.valueOf(1000),
                ContaTipo.POUPANCA,
                "Ligia",
                "12312445212"
        );
        contaDestino = new Conta(
                2L,
                2L,
                5L,
                BigDecimal.valueOf(1000),
                ContaTipo.POUPANCA,
                "Helen",
                "115935785246"
        );

        criarNovaConta.execute(contaOrigem);
        criarNovaConta.execute(contaDestino);
    }

    @Test
    public void deveTransferirComSucesso() throws Exception {
        // Given

        // When
        // Conta contaOrigemAtualizada = transferirValor.execute(contaOrigem, contaDestino, BigDecimal.valueOf(100.15));
        List<Conta> contasAtualizadas = transferirValor.execute(
                contaOrigem,
                contaDestino,
                BigDecimal.valueOf(100.15)
        );

        Conta contaOrigemAtualizada = contasAtualizadas
                .stream()
                .filter(c -> c.getCpf().equals(contaOrigem.getCpf()))
                .findFirst()
                .get();

        Conta contaDestinoAtualizada = contasAtualizadas
                .stream()
                .filter(c -> c.getCpf().equals(contaDestino.getCpf()))
                .findFirst()
                .get();

        Conta finalContaOrigemAtualizada = contaOrigemAtualizada;
        Conta finalContaDestinoAtualizada = contaDestinoAtualizada;

        Transacao transacaoContaOrigem = transacaoRepository.findByContaId(finalContaOrigemAtualizada.getId());
        Transacao transacaoContaDestino = transacaoRepository.findByContaId(finalContaDestinoAtualizada.getId());

        // Then
        // Assertions.assertEquals(BigDecimal.valueOf(899.85), contaOrigemAtualizada.getSaldo());
        Assertions.assertAll(
                "Testando saldos das contas",
                () -> assertEquals(BigDecimal.valueOf(899.85), finalContaOrigemAtualizada.getSaldo()),
                () -> assertEquals(BigDecimal.valueOf(1100.15), finalContaDestinoAtualizada.getSaldo())
        );
        Assertions.assertAll(
                "Testando as informações das transações das contas",
                () -> assertEquals(finalContaOrigemAtualizada, transacaoContaOrigem.getConta()),
                () -> assertEquals(TransacaoTipo.ENVIA_VALOR, transacaoContaOrigem.getTipo()),
                () -> assertEquals(finalContaDestinoAtualizada, transacaoContaDestino.getConta()),
                () -> assertEquals(TransacaoTipo.RECEBE_VALOR, transacaoContaDestino.getTipo())
        );
    }

    @Test
    public void deveLancarUmaExceptionCasoUsuarioForneceContaOrigemInvalida() {
        // Given

        // When
        contaOrigem.setCpf("123");

        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> transferirValor.execute(contaOrigem, null, BigDecimal.valueOf(100.00))
        );

        // Then
        Assertions.assertEquals(
                "A conta de origem informada é inválida!",
                throwable.getLocalizedMessage()
        );
    }

    @Test
    public void deveLancarUmaExceptionCasoUsuarioForneceContaDestinoInvalida() {
        // Given

        // When
        contaDestino.setCpf("123");

        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> transferirValor.execute(contaOrigem, contaDestino, BigDecimal.valueOf(100.00))
        );

        // Then
        Assertions.assertEquals(
                "A conta de destino informada é inválida!",
                throwable.getLocalizedMessage()
        );
    }

    @Test
    public void deveLancarUmaExceptionCasoUsuarioForneceValorMaiorQueSaldoDaContaOrigem() {
        // Given

        // When
        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> transferirValor.execute(contaOrigem, contaDestino, BigDecimal.valueOf(3000.00))
        );

        // Then
        Assertions.assertEquals(
                "Não é possível sacar valor informado!",
                throwable.getLocalizedMessage()
        );
    }
}
