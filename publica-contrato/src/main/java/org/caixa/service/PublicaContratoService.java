package org.caixa.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.caixa.dto.PublicacaoRequest;
import org.caixa.dto.ValidacaoResponse;
import org.caixa.restclient.ValidacaoClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.logging.Logger;


@RequestScoped
public class PublicaContratoService {

    @Inject
    @RestClient
    ValidacaoClient validacaoClient;


    @Transactional
    public Integer publicar(PublicacaoRequest dto) {

        Log.info("Chegou no service /hello/publica");
        ValidacaoResponse resposta = validacaoClient.validar(dto);

        Log.info("passando na validacao");


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
