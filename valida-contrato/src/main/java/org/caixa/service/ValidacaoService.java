package org.caixa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.caixa.dto.ContratoParaValidacaoEvent;
import org.caixa.dto.ContratoValidadoEvent;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ValidacaoService {

    private static final BigDecimal LIMITE_MAXIMO = new BigDecimal("10000000");

    @Inject
    ObjectMapper objectMapper;

    private final Map<String, String> resultadosPorEventId = new ConcurrentHashMap<>();

    @Incoming("contrato-validacao-solicitada")
    @Outgoing("contrato-validacao-resultado")
    @Blocking
    public String validar(String payload) {
        ContratoParaValidacaoEvent evento = desserializar(payload);
        if(evento != null && evento.eventId() != null){
            String jaProcessado = resultadosPorEventId.get(evento.eventId());
            if(jaProcessado != null){
                Log.infof("Dentro da validação -- evento já processado");
                return jaProcessado;
            }
        }


        Log.infof("Dentro da validação -- evento ainda nao processado");
        String status = "APROVADO";
        String motivo = "Contrato validado com sucesso";

        if(evento == null || evento.payload() == null || evento.payload().valor() == null){
            Log.infof("Dentro da validação -- evento reprovado");
            status = "REPROVADO";
            motivo = "Evento invalido para validacao";
        } else if (evento.payload().valor().compareTo(LIMITE_MAXIMO)>0)
        {
            Log.infof("Dentro da validação -- evento reprovado");
            status = "REPROVADO";
            motivo = "Valor acima do limite de demonstracao";
        }

        UUID id = evento != null && evento.payload() != null ? evento.payload().id() : null;
        String correlationId = evento != null ? evento.correlationId() : UUID.randomUUID().toString();

        ContratoValidadoEvent resultado = new ContratoValidadoEvent(
                UUID.randomUUID().toString(),
                "ContratoValidadoEvent",
                1,
                OffsetDateTime.now(),
                correlationId,
                "valida-contratos",
                new ContratoValidadoEvent.Payload(
                        id,
                        status,
                        motivo,
                        OffsetDateTime.now()
                )
        );

        String serializado = serializar(resultado);
        if(evento != null && evento.eventId() != null){
            resultadosPorEventId.putIfAbsent(evento.eventId(), serializado);
        }
        Log.infof("finalizando -- evento aprovado");
        return serializado;
    }

    private ContratoParaValidacaoEvent desserializar(String payload) {
        try{
            return objectMapper.readValue(payload, ContratoParaValidacaoEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String serializar(ContratoValidadoEvent resultado){
        try {
            return objectMapper.writeValueAsString(resultado);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}