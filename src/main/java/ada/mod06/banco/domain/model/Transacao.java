package ada.mod06.banco.domain.model;

import ada.mod06.banco.domain.model.enums.TransacaoTipo;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="conta_id")
    private Conta conta;
    // private Conta contaOrigem;
    // private Conta contaDestino;
    @Enumerated(EnumType.STRING)
    private TransacaoTipo tipo;
    private LocalDateTime data;

    public Transacao(
            Conta conta,
            TransacaoTipo tipo,
            LocalDateTime data
    ) {
        this.conta = conta;
        this.tipo = tipo;
        this.data = data;
    }
}
