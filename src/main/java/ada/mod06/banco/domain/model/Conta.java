package ada.mod06.banco.domain.model;

import ada.mod06.banco.domain.model.enums.ContaTipo;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "contas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id", "agencia"})
@ToString
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long agencia;
    private Long digito;
    private BigDecimal saldo;
    @Enumerated(EnumType.STRING)
    private ContaTipo tipo;
    private String titular;
    private String cpf;

    public Conta(
            long agencia,
            long digito,
            BigDecimal saldo,
            ContaTipo tipo,
            String titular,
            String cpf
    ) {
        this.agencia = agencia;
        this.digito = digito;
        this.saldo = saldo;
        this.tipo = tipo;
        this.titular = titular;
        this.cpf = cpf;
    }

    public void depositar(BigDecimal valor) {
        this.saldo = this.saldo.add(valor);
    }

    public void sacar(BigDecimal valor) {
        this.saldo = this.saldo.subtract(valor);
    }
}
