package ada.mod06.banco.domain.usecase;

import ada.mod06.banco.domain.gateway.ContaGateway;
import ada.mod06.banco.domain.model.Conta;
import ada.mod06.banco.dummy.ContaGatewayDummyImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class CriarNovaContaDummyTest {
    /*@Test
    public void deveLancarUmaExceptionCasoUsuarioJaPossuaUmaConta() {
        // Given
        ContaGateway contaGatewayDummy = new ContaGatewayDummyImpl();
        CriarNovaConta criarNovaConta = new CriarNovaConta(contaGatewayDummy);
        Conta novaConta = new Conta(
                102L,
                0001L,
                1235L,
                new BigDecimal("2000.00"),
                "Bob",
                "98765432100"
        );

        // When
        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> criarNovaConta.execute(novaConta)
        );

        // Then
        Assertions.assertEquals(
                "Usuário já possui uma conta!",
                throwable.getLocalizedMessage()
        );
    }

    @Test
    public void deveCriarUmaContaComSucesso() throws Exception {
        // Given
        ContaGateway contaGatewayDummy = new ContaGatewayDummyImpl();
        CriarNovaConta criarNovaConta = new CriarNovaConta(contaGatewayDummy);
        Conta novaConta = new Conta(
                102L,
                0001L,
                1235L,
                new BigDecimal("2000.00"),
                "Bob",
                "98765432100"
        );

        // Then
        Conta contaCadastrada = criarNovaConta.execute(novaConta);

        // Then
        Assertions.assertAll(
                () -> Assertions.assertEquals(102L, contaCadastrada.getId()),
                () -> Assertions.assertEquals("Bob", contaCadastrada.getTitular())
        );
    }*/
}
