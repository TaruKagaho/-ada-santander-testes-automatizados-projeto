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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SacarValorTest {
    @Mock
    private ContaGateway contaGateway;

    @Mock
    private TransacaoGateway transacaoGateway;

    @InjectMocks
    private SacarValor sacarValor;

    @Test
    public void deveSacarComSucesso() throws Exception {
        // Given
        Conta novaConta = new Conta(
                1L,
                2L,
                3L,
                BigDecimal.valueOf(1000),
                ContaTipo.POUPANCA,
                "Ligia",
                "12312445212"
        );
        LocalDateTime data = LocalDateTime.now();
        Transacao transacao = new Transacao(
                novaConta,
                TransacaoTipo.SAQUE,
                data
        );

        // When
        // Mocks response
        when(contaGateway.buscarPorCpf(novaConta.getCpf())).thenReturn(novaConta);
        // when(contaGateway.salvar(novaConta)).thenReturn(novaConta);
        when(contaGateway.salvar(any())).thenReturn(novaConta);
        when((transacaoGateway.salvar(any()))).thenReturn(transacao);

        Conta contaAtualizada = sacarValor.execute(novaConta, BigDecimal.valueOf(100.15));

        transacao.setConta(contaAtualizada);

        // Then
        Assertions.assertEquals(BigDecimal.valueOf(899.85), contaAtualizada.getSaldo());
        Assertions.assertAll(
                "Testando informações da transação",
                () -> Assertions.assertEquals(transacao.getConta(), contaAtualizada),
                () -> Assertions.assertEquals(transacao.getTipo(), TransacaoTipo.SAQUE),
                () -> Assertions.assertEquals(transacao.getData(), data)
        );
    }

    @Test
    public void deveLancarUmaExceptionCasoUsuarioForneceUmaContaInvalida() {
        // Given
        Conta novaConta = new Conta(
                102L,
                0001L,
                1235L,
                BigDecimal.valueOf(2000.00),
                ContaTipo.CORRENTE,
                "Bob",
                "98765432100"
        );

        // When
        when(contaGateway.buscarPorCpf(novaConta.getCpf())).thenReturn(null);

        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> sacarValor.execute(novaConta, BigDecimal.valueOf(2000.00))
        );

        // Then
        Assertions.assertEquals(
                "A conta informada é inválida!",
                throwable.getLocalizedMessage()
        );
        verify(contaGateway, times(1)).buscarPorCpf(novaConta.getCpf());
        verify(contaGateway, never()).salvar(novaConta);
    }

    @Test
    public void deveLancarUmaExceptionCasoUsuarioForneceUmValorMaiorQueSaldo() throws Exception {
        // Given
        Conta novaConta = new Conta(
                102L,
                0001L,
                1235L,
                BigDecimal.valueOf(2000.00),
                ContaTipo.CORRENTE,
                "Bob",
                "98765432100"
        );

        // When
        when(contaGateway.buscarPorCpf(novaConta.getCpf())).thenReturn(novaConta);

        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> sacarValor.execute(novaConta, BigDecimal.valueOf(2100.00))
        );

        // Then
        Assertions.assertEquals(
                "Não é possível sacar valor informado!",
                throwable.getLocalizedMessage()
        );
        verify(contaGateway, times(1)).buscarPorCpf(novaConta.getCpf());
        verify(contaGateway, never()).salvar(novaConta);
    }
}
