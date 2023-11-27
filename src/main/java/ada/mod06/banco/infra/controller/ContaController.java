package ada.mod06.banco.infra.controller;

import ada.mod06.banco.domain.model.Conta;
import ada.mod06.banco.domain.usecase.CriarNovaConta;

import ada.mod06.banco.domain.usecase.DepositarValor;
import ada.mod06.banco.infra.dto.DepositoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contas")
public class ContaController {
    private CriarNovaConta criarNovaConta;

    private DepositarValor depositarValor;

    public ContaController(
            CriarNovaConta criarNovaConta,
            DepositarValor depositarValor
    ) {
        this.criarNovaConta = criarNovaConta;
        this.depositarValor = depositarValor;
    }

    @PostMapping
    public ResponseEntity criarConta(@RequestBody Conta conta) throws Exception {
        Conta novaConta;

        try {
            novaConta = criarNovaConta.execute(conta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(novaConta);
    }

    @PostMapping("/depositos")
    public ResponseEntity depositar(@RequestBody DepositoDto depositoDto) {
        Conta contaAtualizada;

        try {
            contaAtualizada = depositarValor.execute(depositoDto.conta(), depositoDto.valor());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(contaAtualizada);
    }
}
