package ada.mod06.banco.infra.gateway.http;

import ada.mod06.banco.domain.gateway.EmailGateway;

import org.springframework.stereotype.Component;

@Component
public class EmailGatewayHttp implements EmailGateway {
    @Override
    public void send(String cpf) {
        System.out.println("Enviando email...");
    }
}
