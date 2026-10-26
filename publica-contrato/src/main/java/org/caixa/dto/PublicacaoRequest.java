package org.caixa.dto;


import java.math.BigDecimal;
import java.time.LocalDate;

public record PublicacaoRequest(
        String nomeFuncao,
        String periodicidade,
        BigDecimal valor,
        LocalDate dataInicio,
        LocalDate dataFim
) {
}
