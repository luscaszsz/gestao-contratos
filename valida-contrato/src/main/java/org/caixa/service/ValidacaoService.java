package org.caixa.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.caixa.dto.PublicacaoRequest;

@ApplicationScoped
public class ValidacaoService {

    public boolean validar(PublicacaoRequest contrato) {
        if (contrato == null) {
            return false;
        }
        Log.info("Chegou no service validar");

        return contrato.valor() != null
                && contrato.valor().signum() > 0;
    }
}