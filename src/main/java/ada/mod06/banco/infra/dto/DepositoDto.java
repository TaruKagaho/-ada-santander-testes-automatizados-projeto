package ada.mod06.banco.infra.dto;

import ada.mod06.banco.domain.model.Conta;

import java.math.BigDecimal;

public record DepositoDto(
        Conta conta,
        BigDecimal valor
) {
}
