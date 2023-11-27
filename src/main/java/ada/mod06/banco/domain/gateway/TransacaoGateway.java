package ada.mod06.banco.domain.gateway;

import ada.mod06.banco.domain.model.Transacao;

public interface TransacaoGateway {
    Transacao salvar(Transacao transacao);
    // void salvar(Transacao transacao);

    Transacao buscarPorContaId(Long contaId);
}
