package org.caixa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.caixa.dto.*;
import org.caixa.model.ProcessedEventModel;
import org.caixa.model.PublicacaoContratoModel;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@ApplicationScoped
public class PublicaContratoService {

    private static final int MAX_TENTATIVAS_ENVIO = 4;
    private static final long BACKOFF_INICIAL_MS = 200L;

    @Inject
    ObjectMapper objectMapper;

    @Channel("contrato-validacao-solicitada")
    Emitter<String> solicitacaoEmitter;


    @Transactional
    public PublicacaoResponse solicitarPublicacao(PublicacaoRequest dto, String indepotencyKey) {


        // Avaliação de evento ja publicado
        if(indepotencyKey != null && !indepotencyKey.isBlank()){
            PublicacaoContratoModel existente = PublicacaoContratoModel.find("idempotencyKey", indepotencyKey).firstResult();
            if(existente != null){
                return new PublicacaoResponse(
                        existente.id,
                        existente.correlationId,
                        existente.statusPublicacao,
                        existente.statusValidacao
                );
            }
        }

        // criacao de novo evento
        UUID id = UUID.randomUUID();
        String correlationId = UUID.randomUUID().toString();
        OffsetDateTime agora = OffsetDateTime.now();

        //crição de novo contrato
        PublicacaoContratoModel model = new PublicacaoContratoModel();
        model.id = id;
        model.nomeFuncao = dto.nomeFuncao();
        model.periodicidade = dto.periodicidade();
        model.valor = dto.valor();
        model.dataInicio = dto.dataInicio();
        model.dataFim = dto.dataFim();
        model.statusPublicacao = "VALIDANDO";
        model.statusValidacao = "PENDENTE";
        model.correlationId = correlationId;
        model.idempotencyKey = indepotencyKey;
        model.tentativasValidacao = 0;
        model.motivo = null;
        model.versao = 1;
        model.ultimaAtualizacao = agora;
        //persistencia com pendencia
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
        enviarEventoComRetry(event, model);

        return new PublicacaoResponse(id, correlationId, model.statusPublicacao, model.statusValidacao);
    }

    private void enviarEventoComRetry(ContratoParaValidacaoEvent event, PublicacaoContratoModel model) {
        try{
            String json = objectMapper.writeValueAsString(event);
            for(int tentativa = 1; tentativa <= MAX_TENTATIVAS_ENVIO; tentativa++){
                try{
                    solicitacaoEmitter.send(json).toCompletableFuture().join();
                    model.tentativasValidacao = tentativa;
                    return;
                }catch (Exception ex){
                    model.tentativasValidacao = tentativa;
                    if(tentativa == MAX_TENTATIVAS_ENVIO){
                        model.statusValidacao = "ERRO_PUBLICACAO";
                        model.motivo = "Falha por retry";
                        model.ultimaAtualizacao = OffsetDateTime.now();
                        throw new RuntimeException("Falha por retry", ex);
                    }
                }
                esperarBackoff(tentativa);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void esperarBackoff(int tentativa) {
        long delayMs = BACKOFF_INICIAL_MS * (1L << (tentativa - 1));
        try {
            TimeUnit.MICROSECONDS.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
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
            // erro na leitura
            return;
        }

        // caso de evento vazio
        if(evento.eventId() == null || evento.eventId().isBlank()){
            return;
        }

        // verificação de evento duplicado
        ProcessedEventModel eventoProcessado = ProcessedEventModel.findById(evento.eventId());
        if (eventoProcessado != null){
            //evento duplicado, ignora
            return;
        }
        if(evento.payload() == null || evento.payload().id() == null){
            // evento de validação invalido sem id, ignora
            return;
        }

        PublicacaoContratoModel model = PublicacaoContratoModel.findById(evento.payload().id());
        if(model == null){
            registrarEventoProcessado(evento);
            return;
        }

        model.statusPublicacao = Objects.requireNonNull(evento.payload().statusValidacao(), "PENDENTE");
        model.motivo = evento.payload().motivo();
        model.statusValidacao = "APROVADO".equals(evento.payload().statusValidacao()) ? "APROVADO" : "REPROVADA";
        model.versao = model.versao + 1;
        model.ultimaAtualizacao = OffsetDateTime.now();
        registrarEventoProcessado(evento);
    }

    private void registrarEventoProcessado(ContratoValidadoEvent evento) {
        ProcessedEventModel processado = new ProcessedEventModel();
        processado.eventId = evento.eventId();
        processado.eventType = evento.eventType() != null ? evento.eventType() : "ContratoValidadoEvent";
        processado.processedAt = OffsetDateTime.now();
        processado.persist();
    }
}
