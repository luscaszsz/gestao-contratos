package org.caixa.dto;

import com.aayushatharva.brotli4j.common.annotations.Local;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PublicacaoRequest(
        String funcao,
        BigDecimal valor,
        LocalDate dataInicio,
        LocalDate dataFim
) {
}
