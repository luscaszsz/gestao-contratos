package org.caixa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.caixa.dto.*;
import org.caixa.model.PublicacaoContratoModel;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import java.time.OffsetDateTime;
import java.util.UUID;


@ApplicationScoped
public class PublicaContratoService {

    @Inject
    ObjectMapper objectMapper;

    @Channel("contrato-validacao-solicitada")
    Emitter<String> solicitacaoEmitter;


    @Transactional
    public PublicacaoResponse solicitarPublicacao(PublicacaoRequest dto) {

        UUID id = UUID.randomUUID();
        String correlationId = UUID.randomUUID().toString();
        OffsetDateTime agora = OffsetDateTime.now();

        PublicacaoContratoModel model = new PublicacaoContratoModel();
        model.id = id;
        model.nomeFuncao = dto.nomeFuncao();
        model.periodicidade = dto.periodicidade();
        model.valor = dto.valor();
        model.dataInicio = dto.dataInicio();
        model.dataFim = dto.dataFim();
        model.statusPublicacao = "VALIDANDO";
        model.statusValidacao = "PENDENTE";
        model.motivo = null;
        model.versao = 1;
        model.ultimaAtualizacao = agora;
        model.persist();

        ContratoParaValidacaoEvent event = new ContratoParaValidacaoEvent(
                UUID.randomUUID().toString(),
                "ContratoParaValidacaoEvent",
                1,
                agora,
                correlationId,
                "publica-contratos",
                new ContratoParaValidacaoEvent.Payload(
                        id,
                        dto.nomeFuncao(),
                        dto.periodicidade(),
                        dto.valor(),
                        dto.dataInicio(),
                        dto.dataFim()
                )
        );
        try{
            solicitacaoEmitter.send(objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        Log.info("Chegou no service /hello/publica");

        Log.info("passando na validacao");

        return new PublicacaoResponse(id, correlationId, model.statusPublicacao, model.statusValidacao);
    }


    @Transactional
    public PublicacaoStatusResponse consultarStatus(String idTexto) {
        UUID id = UUID.fromString(idTexto);
        PublicacaoContratoModel model = PublicacaoContratoModel.findById(id);
        if(model == null){
            throw new NotFoundException("nao existe status para o id");
        }
        return new PublicacaoStatusResponse(
                model.id,
                model.nomeFuncao,
                model.periodicidade,
                model.valor,
                model.dataInicio,
                model.dataFim,
                model.statusPublicacao,
                model.statusValidacao,
                model.motivo,
                model.ultimaAtualizacao
        );
    }


    @Incoming("contrato-validacao-resultado")
    @Blocking
    @Transactional
    public void consumirResultadoValidacao(String payload){
        ContratoValidadoEvent evento;
        try{
            evento = objectMapper.readValue(payload, ContratoValidadoEvent.class);
        } catch (JsonProcessingException e) {
            return;
        }

        PublicacaoContratoModel model = PublicacaoContratoModel.findById(evento.payload().id());
        if(model == null){
            return;
        }

        model.statusValidacao = evento.payload().statusValidacao();
        model.motivo = evento.payload().motivo();
        model.statusValidacao = "APROVADO".equals(evento.payload().statusValidacao()) ? "APROVADO" : "REPROVADA";
        model.versao = model.versao + 1;
        model.ultimaAtualizacao = OffsetDateTime.now();
    }
}
