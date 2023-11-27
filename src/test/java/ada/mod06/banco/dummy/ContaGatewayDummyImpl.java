package ada.mod06.banco.dummy;

import ada.mod06.banco.domain.gateway.ContaGateway;
import ada.mod06.banco.domain.model.Conta;
import ada.mod06.banco.domain.model.enums.ContaTipo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ContaGatewayDummyImpl implements ContaGateway {
    private Map<String, Conta> contas = new HashMap<>();

    public ContaGatewayDummyImpl() {
        contas.put(
                "12345678900",
                new Conta(
                        101L,
                        0001L,
                        1234L,
                        new BigDecimal("1000.00"),
                        ContaTipo.CORRENTE,
                        "Alice",
                        "12345678900"
                )
        );
        contas.put(
                "12312445212",
                new Conta(
                        1231L,
                        0002L,
                        1L,
                        BigDecimal.valueOf(10000),
                        ContaTipo.POUPANCA,
                        "Ligia",
                        "12312445212")
        );
        contas.put(
                "98765432100",
                new Conta(
                        102L,
                        0001L,
                        1235L,
                        new BigDecimal("2000.00"),
                        ContaTipo.CORRENTE,
                        "Bob",
                        "98765432100")
        );
    }

    @Override
    public Conta buscarPorCpf(String cpf) {
        return contas.get(cpf);
    }

    @Override
    public Conta salvar(Conta conta) {
        return contas.put(conta.getCpf(), conta);
    }
}
