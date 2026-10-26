package org.caixa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PublicacaoStatusResponse(
        UUID id,
        String nomeFuncao,
        String periodicidade,
        BigDecimal valor,
        LocalDate dataInicio,
        LocalDate dataFim,
        String statusPublicacao,
        String statusValidacao,
        String motivo,
        OffsetDateTime atualizadoEm
) {
}
