package org.caixa.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ContratoValidadoEvent(
        String eventId,
        String eventType,
        int eventVersiona,
        OffsetDateTime occuredAt,
        String correlationId,
        String source,
        Payload payload
) {
    public record Payload(
            UUID uuid,
            String statusValidacao,
            String motivo,
            OffsetDateTime validadoEm
    ){}
}
