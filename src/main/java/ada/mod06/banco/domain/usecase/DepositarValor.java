package ada.mod06.banco.domain.usecase;

import ada.mod06.banco.domain.gateway.ContaGateway;
import ada.mod06.banco.domain.gateway.TransacaoGateway;
import ada.mod06.banco.domain.model.Conta;
import ada.mod06.banco.domain.model.Transacao;
import ada.mod06.banco.domain.model.enums.TransacaoTipo;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DepositarValor {
    private ContaGateway contaGateway;

    private TransacaoGateway transacaoGateway;

    public DepositarValor(
            ContaGateway contaGateway,
            TransacaoGateway transacaoGateway
    ) {
        this.contaGateway = contaGateway;
        this.transacaoGateway = transacaoGateway;
    }

    public Conta execute(Conta conta, BigDecimal valor) throws Exception {
        // Verifica se a conta existe
        if (contaGateway.buscarPorCpf(conta.getCpf()) == null) {
            throw new Exception("A conta informada é inválida!");
        }

        // Adiciona valor ao saldo
        conta.depositar(valor);

        // Salva conta no banco de dados
        Conta contaAtualizada = contaGateway.salvar(conta);

        // TODO: isolar a parte de transação
        // Criar transação para histórico
        Transacao transacao = new Transacao(
                contaAtualizada,
                TransacaoTipo.DEPOSITO,
                LocalDateTime.now()
        );

        // Salva transição no banco de dados
        transacaoGateway.salvar(transacao);

        // Retorna conta atualizada
        return conta;
    }
}
