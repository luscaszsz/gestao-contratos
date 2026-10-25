package org.caixa.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.caixa.dto.PublicacaoRequest;

@ApplicationScoped
public class ValidacaoService {

    public boolean validar(PublicacaoRequest contrato) {
        if (contrato == null) {
            return false;
        }

        return contrato.valor() != null
                && contrato.valor().signum() > 0;
    }
}