package org.caixa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ContratoParaValidacaoEvent(
        String eventoId,
        Integer eventoTipo,
        int versao,
        OffsetDateTime offsetDateTime,
        String correlationId,
        String source,
        Payload payload
) {
    public record Payload(
            UUID id,
            String funcao,
            BigDecimal valor,
            LocalDate dataInicio,
            LocalDate dataFim){
    }
}
