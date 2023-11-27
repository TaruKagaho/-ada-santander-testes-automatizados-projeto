package ada.mod06.banco.domain.usecase;

import ada.mod06.banco.domain.gateway.ContaGateway;
import ada.mod06.banco.domain.gateway.EmailGateway;
import ada.mod06.banco.domain.model.Conta;
import ada.mod06.banco.domain.model.enums.ContaTipo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

//Inciar os mocks sem necessidade de iniciar no beforeEach:
@ExtendWith(MockitoExtension.class)
public class CriarNovaContaIntegrationTest {
    // mvn repository - Mockito Core
    @Mock
    private ContaGateway contaGateway;

    @Mock
    private EmailGateway emailGateway;

    @InjectMocks
    private CriarNovaConta criarNovaConta;

    /*@BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
    }*/

    @Test
    public void deveLancarUmaExceptionCasoUsuarioJaPossuaUmaConta() {
        // Given
        // ContaGateway contaGateway = new ContaGatewayDummyImpl();
        // ContaGateway contaGateway = mock(ContaGateway.class);
        // CriarNovaConta criarNovaConta = new CriarNovaConta(contaGateway);
        Conta novaConta = new Conta(
                102L,
                0001L,
                1235L,
                new BigDecimal("2000.00"),
                ContaTipo.CORRENTE,
                "Bob",
                "98765432100"
        );

        // When
        when(contaGateway.buscarPorCpf(novaConta.getCpf())).thenReturn(novaConta);

        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> criarNovaConta.execute(novaConta)
        );

        // Then
        Assertions.assertEquals(
                "Usuário já possui uma conta!",
                throwable.getLocalizedMessage()
        );
        verify(contaGateway, times(1)).buscarPorCpf(novaConta.getCpf());
        verify(contaGateway, never()).salvar(novaConta);
    }

    @Test
    public void deveCriarUmaContaComSucesso() throws Exception {
        // Given
        // ContaGateway contaGateway = new ContaGatewayDummyImpl();
        // ContaGateway contaGateway = mock(ContaGateway.class);
        // CriarNovaConta criarNovaConta = new CriarNovaConta(contaGateway);
        Conta novaConta = new Conta(
                102L,
                0001L,
                1235L,
                new BigDecimal("2000.00"),
                ContaTipo.CORRENTE,
                "Bob",
                "98765432100"
        );

        // When
        // Conta contaCadastrada = criarNovaConta.execute(novaConta);

        // Mocks response
        when(contaGateway.buscarPorCpf(novaConta.getCpf())).thenReturn(null);
        when(contaGateway.salvar(novaConta)).thenReturn(novaConta);
        // when(contaGateway.salvar(any())).thenReturn(conta);
        // verify(contaGateway, never()).salvar(novaConta);
        doNothing().when(emailGateway).send(novaConta.getCpf());

        criarNovaConta.execute(novaConta);

        // Then
        Assertions.assertAll(
                () -> Assertions.assertEquals(102L, novaConta.getId()),
                () -> Assertions.assertEquals("Bob", novaConta.getTitular())
        );
        verify(contaGateway, times(1)).buscarPorCpf(novaConta.getCpf());
        verify(contaGateway, times(1)).salvar(any());
        verify(emailGateway, times(1)).send(novaConta.getCpf());
    }
}
