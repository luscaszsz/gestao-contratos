package org.caixa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ContratoParaValidacaoEvent(
        String eventId,
        String eventTipo,
        int eventVersion,
        OffsetDateTime occuredAt,
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
