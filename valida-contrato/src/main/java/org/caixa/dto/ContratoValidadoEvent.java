package org.caixa.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ContratoValidadoEvent(
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
            String statusValidacao,
            String motivo,
            OffsetDateTime validadoEm
    ){}
}
