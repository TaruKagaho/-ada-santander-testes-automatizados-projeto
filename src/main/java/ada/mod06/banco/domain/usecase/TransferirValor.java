package ada.mod06.banco.domain.usecase;

import ada.mod06.banco.domain.gateway.ContaGateway;
import ada.mod06.banco.domain.gateway.TransacaoGateway;
import ada.mod06.banco.domain.model.Conta;
import ada.mod06.banco.domain.model.Transacao;
import ada.mod06.banco.domain.model.enums.TransacaoTipo;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransferirValor {
    private ContaGateway contaGateway;

    private TransacaoGateway transacaoGateway;

    public TransferirValor(
            ContaGateway contaGateway,
            TransacaoGateway transacaoGateway
    ) {
        this.contaGateway = contaGateway;
        this.transacaoGateway = transacaoGateway;
    }

    public List<Conta> execute(
            Conta contaOrigem,
            Conta contaDestino,
            BigDecimal valor
    ) throws Exception {
        // Verifica se a contaOrigem existe
        if (contaGateway.buscarPorCpf(contaOrigem.getCpf()) == null) {
            throw new Exception("A conta de origem informada é inválida!");
        }

        // Verifica se a contaDestino existe
        if (contaGateway.buscarPorCpf(contaDestino.getCpf()) == null) {
            throw new Exception("A conta de destino informada é inválida!");
        }

        // Averigua se valor é maior que saldo
        if ( contaOrigem.getSaldo().compareTo(valor) < 0  ) {
            throw new Exception("Não é possível sacar valor informado!");
        }

        // Remove valor do saldo da contaOrigem
        contaOrigem.sacar(valor);

        // Adiciona valor ao saldo da contaDestino
        contaDestino.depositar(valor);

        // Salva contas no banco de dados
        Conta contaOrigemAtualizada = contaGateway.salvar(contaOrigem);
        Conta contaDestinoAtualizada = contaGateway.salvar(contaDestino);

        // TODO: isolar a parte de transação
        // Criar transação para histórico
        LocalDateTime data = LocalDateTime.now();
        Transacao transacaoContaOrigem = new Transacao(
                contaOrigemAtualizada,
                TransacaoTipo.ENVIA_VALOR,
                data
        );
        Transacao transacaoContaDestino = new Transacao(
                contaDestinoAtualizada,
                TransacaoTipo.RECEBE_VALOR,
                data
        );

        // Salva transições no banco de dados
        transacaoGateway.salvar(transacaoContaOrigem);
        transacaoGateway.salvar(transacaoContaDestino);

        // Retorna com as contas atualizadas
        List<Conta> contasAtualizadas = new ArrayList<>();
        contasAtualizadas.add(contaOrigemAtualizada);
        contasAtualizadas.add(contaDestinoAtualizada);

        return contasAtualizadas;
        // return contaOrigemAtualizada;
    }
}
