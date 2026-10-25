package org.caixa.dto;

import java.util.UUID;

public record PublicacaoResponse(
        UUID id,
        String correlationId,
        String statusPublicacao,
        String statusValidacao
) {
}
