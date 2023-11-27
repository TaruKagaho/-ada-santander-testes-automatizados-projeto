package ada.mod06.banco.infra.gateway.bd;

import ada.mod06.banco.domain.gateway.TransacaoGateway;
import ada.mod06.banco.domain.model.Transacao;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransacaoGatewayDatabase implements TransacaoGateway {
    private final TransacaoRepository transacaoRepository;

    public TransacaoGatewayDatabase(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    @Override
    public Transacao salvar(Transacao transacao) {
        return transacaoRepository.save(transacao);
    }

    @Override
    public Transacao buscarPorContaId(Long contaId) {
        return transacaoRepository.findByContaId(contaId);
    }

    public List<Transacao>  buscarTodasPorContaId(Long contaId) {
        return transacaoRepository.findAllByContaId(contaId);
    }
}
