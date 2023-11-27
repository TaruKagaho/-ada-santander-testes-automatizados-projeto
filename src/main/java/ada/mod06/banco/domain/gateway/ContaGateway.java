package ada.mod06.banco.domain.gateway;

import ada.mod06.banco.domain.model.Conta;

public interface ContaGateway {
    Conta buscarPorCpf(String cpf);

    Conta salvar(Conta conta);
}
