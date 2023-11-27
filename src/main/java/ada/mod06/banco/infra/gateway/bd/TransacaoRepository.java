package ada.mod06.banco.infra.gateway.bd;

import ada.mod06.banco.domain.model.Transacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    Transacao findByContaId(Long contaId);

    List<Transacao> findAllByContaId(Long contaId);
}
