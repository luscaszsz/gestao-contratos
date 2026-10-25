package org.caixa.dto;

import java.math.BigDecimal;

public record ContratoDTO(
        String numero,
        String contratante,
        BigDecimal valor
) {}