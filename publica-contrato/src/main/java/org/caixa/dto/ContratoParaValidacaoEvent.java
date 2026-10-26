package org.caixa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ContratoParaValidacaoEvent(
        String eventId,
        String eventType,
        int eventVersion,
        OffsetDateTime occurredAt,
        String correlationId,
        String source,
        Payload payload
) {
    public record Payload(
            UUID id,
            String nomeFuncao,
            String periodicidade,
            BigDecimal valor,
            LocalDate dataInicio,
            LocalDate dataFim){
    }
}
