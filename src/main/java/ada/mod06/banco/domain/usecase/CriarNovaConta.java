package ada.mod06.banco.domain.usecase;

import ada.mod06.banco.domain.gateway.ContaGateway;
import ada.mod06.banco.domain.gateway.EmailGateway;
import ada.mod06.banco.domain.model.Conta;

import org.springframework.stereotype.Component;

@Component
public class CriarNovaConta {
    private ContaGateway contaGateway;

    private EmailGateway emailGateway;

    public CriarNovaConta(
            ContaGateway contaGateway,
            EmailGateway emailGateway
    ) {
        this.contaGateway = contaGateway;
        this.emailGateway = emailGateway;
    }

    public Conta execute(Conta conta) throws Exception {
        /*
        * Validar se o usuário já possui uma conta.
        * Se possuir vamos lançar uma exception.
        * Caso não possua, criar nova conta.
        * */
        if (contaGateway.buscarPorCpf(conta.getCpf()) != null) {
            throw new Exception("Usuário já possui uma conta!");
        }

        Conta novaConta = contaGateway.salvar(conta);

        emailGateway.send(conta.getCpf());

        return novaConta;
    }
}
