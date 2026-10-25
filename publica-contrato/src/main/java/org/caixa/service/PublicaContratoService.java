package org.caixa.service;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.caixa.dto.PublicacaoRequest;
import org.caixa.dto.ValidacaoResponse;
import org.caixa.model.ContratoModel;
import org.caixa.restclient.ValidacaoClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;


@RequestScoped
public class PublicaContratoService {

    @Inject
    @RestClient
    ValidacaoClient validacaoClient;

    @Transactional
    public Integer publicar(PublicacaoRequest dto) {
        ValidacaoResponse resposta = validacaoClient.validar(dto);

        if (!resposta.valido()) {
            return 1;
        }

//        Contrato contrato = new Contrato();
//        contrato.numero = dto.numero();
//        contrato.contratante = dto.contratante();
//        contrato.valor = dto.valor();
//        contrato.persist();

        return 0;
    }
}
