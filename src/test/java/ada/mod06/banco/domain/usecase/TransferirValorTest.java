package ada.mod06.banco.domain.usecase;

import ada.mod06.banco.domain.gateway.ContaGateway;
import ada.mod06.banco.domain.gateway.TransacaoGateway;
import ada.mod06.banco.domain.model.Conta;
import ada.mod06.banco.domain.model.Transacao;
import ada.mod06.banco.domain.model.enums.ContaTipo;
import ada.mod06.banco.domain.model.enums.TransacaoTipo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferirValorTest {
    @Mock
    private ContaGateway contaGateway;

    @Mock
    private TransacaoGateway transacaoGateway;

    @InjectMocks
    private TransferirValor transferirValor;

    @Test
    public void deveTransferirComSucesso() throws Exception {
        // Given
        Conta contaOrigem = new Conta(
                1L,
                2L,
                3L,
                BigDecimal.valueOf(1000),
                ContaTipo.POUPANCA,
                "Ligia",
                "12312445212"
        );
        Conta contaDestino = new Conta(
                2L,
                2L,
                5L,
                BigDecimal.valueOf(1000),
                ContaTipo.POUPANCA,
                "Helen",
                "115935785246"
        );
        Conta contaOrigemAtualizada;
        Conta contaDestinoAtualizada;
        LocalDateTime data = LocalDateTime.now();
        Transacao transacaoContaOrigem = new Transacao(
                contaOrigem,
                TransacaoTipo.ENVIA_VALOR,
                data
        );
        Transacao transacaoContaDestino = new Transacao(
                contaDestino,
                TransacaoTipo.RECEBE_VALOR,
                data
        );

        // When
        // Mocks response
        when(contaGateway.buscarPorCpf(contaOrigem.getCpf())).thenReturn(contaOrigem);
        when(contaGateway.buscarPorCpf(contaDestino.getCpf())).thenReturn(contaDestino);
        when(contaGateway.salvar(contaOrigem)).thenReturn(contaOrigem);
        when(contaGateway.salvar(contaDestino)).thenReturn(contaDestino);
        // when(transacaoGateway.salvar(transacaoContaOrigem)).thenReturn(transacaoContaOrigem);
        when(transacaoGateway.salvar(any())).thenReturn(transacaoContaOrigem);
        // when(transacaoGateway.salvar(transacaoContaDestino)).thenReturn(transacaoContaDestino);
        when(transacaoGateway.salvar(any())).thenReturn(transacaoContaDestino);

        // Conta contaOrigemAtualizada = transferirValor.execute(contaOrigem, contaDestino, BigDecimal.valueOf(100.15));
        List<Conta> contasAtualizadas = transferirValor.execute(
                contaOrigem,
                contaDestino,
                BigDecimal.valueOf(100.15)
        );

        contaOrigemAtualizada = contasAtualizadas
                .stream()
                .filter(c -> c.getCpf().equals(contaOrigem.getCpf()))
                .findFirst()
                .get();
        contaDestinoAtualizada = contasAtualizadas
                .stream()
                .filter(c -> c.getCpf().equals(contaDestino.getCpf()))
                .findFirst()
                .get();

        Conta finalContaOrigemAtualizada = contaOrigemAtualizada;
        Conta finalContaDestinoAtualizada = contaDestinoAtualizada;

        transacaoContaOrigem.setConta(finalContaOrigemAtualizada);
        transacaoContaDestino.setConta(finalContaDestinoAtualizada);

        // Then
        // Assertions.assertEquals(BigDecimal.valueOf(899.85), contaOrigemAtualizada.getSaldo());
        Assertions.assertAll(
                "Testando saldos das contas",
                () -> assertEquals(BigDecimal.valueOf(899.85), finalContaOrigemAtualizada.getSaldo()),
                () -> assertEquals(BigDecimal.valueOf(1100.15), finalContaDestinoAtualizada.getSaldo())
        );
        Assertions.assertAll(
                "Testando as informações das transações das contas",
                () -> assertEquals(transacaoContaOrigem.getConta(), finalContaOrigemAtualizada),
                () -> assertEquals(transacaoContaOrigem.getTipo(), TransacaoTipo.ENVIA_VALOR),
                () -> assertEquals(transacaoContaOrigem.getData(), data),
                () -> assertEquals(transacaoContaDestino.getConta(), finalContaDestinoAtualizada),
                () -> assertEquals(transacaoContaDestino.getTipo(), TransacaoTipo.RECEBE_VALOR),
                () -> assertEquals(transacaoContaDestino.getData(), data)
        );
    }

    @Test
    public void deveLancarUmaExceptionCasoUsuarioForneceContaOrigemInvalida() {
        // Given
        Conta contaOrigem = new Conta(
                102L,
                0001L,
                1235L,
                BigDecimal.valueOf(2000.00),
                ContaTipo.CORRENTE,
                "Bob",
                "98765432100"
        );

        // When
        when(contaGateway.buscarPorCpf(contaOrigem.getCpf())).thenReturn(null);

        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> transferirValor.execute(contaOrigem, null, BigDecimal.valueOf(100.00))
        );

        // Then
        Assertions.assertEquals(
                "A conta de origem informada é inválida!",
                throwable.getLocalizedMessage()
        );
        verify(contaGateway, times(1)).buscarPorCpf(contaOrigem.getCpf());
        verify(contaGateway, never()).salvar(contaOrigem);
    }

    @Test
    public void deveLancarUmaExceptionCasoUsuarioForneceContaDestinoInvalida() {
        // Given
        Conta contaOrigem = new Conta(
                102L,
                0001L,
                1235L,
                BigDecimal.valueOf(2000.00),
                ContaTipo.CORRENTE,
                "Bob",
                "98765432100"
        );
        Conta contaDestino = new Conta(
                1L,
                2L,
                3L,
                BigDecimal.valueOf(1000),
                ContaTipo.POUPANCA,
                "Ligia",
                "12312445212"
        );

        // When
        when(contaGateway.buscarPorCpf(contaOrigem.getCpf())).thenReturn(contaOrigem);
        when(contaGateway.buscarPorCpf(contaDestino.getCpf())).thenReturn(null);

        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> transferirValor.execute(contaOrigem, contaDestino, BigDecimal.valueOf(100.00))
        );

        // Then
        Assertions.assertEquals(
                "A conta de destino informada é inválida!",
                throwable.getLocalizedMessage()
        );
        verify(contaGateway, times(1)).buscarPorCpf(contaOrigem.getCpf());
        verify(contaGateway, times(1)).buscarPorCpf(contaDestino.getCpf());
    }

    @Test
    public void deveLancarUmaExceptionCasoUsuarioForneceValorMaiorQueSaldoDaContaOrigem() {
        // Given
        Conta contaOrigem = new Conta(
                102L,
                0001L,
                1235L,
                BigDecimal.valueOf(2000.00),
                ContaTipo.CORRENTE,
                "Bob",
                "98765432100"
        );
        Conta contaDestino = new Conta(
                1L,
                2L,
                3L,
                BigDecimal.valueOf(1000),
                ContaTipo.POUPANCA,
                "Ligia",
                "12312445212"
        );

        // When
        when(contaGateway.buscarPorCpf(contaOrigem.getCpf())).thenReturn(contaOrigem);
        when(contaGateway.buscarPorCpf(contaDestino.getCpf())).thenReturn(contaDestino);

        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> transferirValor.execute(contaOrigem, contaDestino, BigDecimal.valueOf(3000.00))
        );

        // Then
        Assertions.assertEquals(
                "Não é possível sacar valor informado!",
                throwable.getLocalizedMessage()
        );
        verify(contaGateway, times(1)).buscarPorCpf(contaOrigem.getCpf());
        verify(contaGateway, times(1)).buscarPorCpf(contaDestino.getCpf());
    }
}
