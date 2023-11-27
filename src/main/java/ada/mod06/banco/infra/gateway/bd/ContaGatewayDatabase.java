package ada.mod06.banco.infra.gateway.bd;

import ada.mod06.banco.domain.gateway.ContaGateway;
import ada.mod06.banco.domain.model.Conta;

import org.springframework.stereotype.Component;

@Component
public class ContaGatewayDatabase implements ContaGateway {
    private final ContaRepository contaRepository;

    public ContaGatewayDatabase(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    @Override
    public Conta buscarPorCpf(String cpf) {
        return contaRepository.findByCpf(cpf);
    }

    @Override
    public Conta salvar(Conta conta) {
        return contaRepository.save(conta);
    }
}
