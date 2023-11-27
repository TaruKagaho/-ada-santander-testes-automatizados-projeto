package ada.mod06.banco.infra.gateway.bd;

import ada.mod06.banco.domain.model.Conta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    Conta findByCpf(String cpf);
}
