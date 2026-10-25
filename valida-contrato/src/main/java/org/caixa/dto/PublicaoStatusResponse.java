package org.caixa.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PublicaoStatusResponse(
        UUID id,
        String nomeFuncao,
        String statusPublicacaao,
        String statusValidacao,
        String motivo,
        OffsetDateTime atualziadoEm
) {
}
