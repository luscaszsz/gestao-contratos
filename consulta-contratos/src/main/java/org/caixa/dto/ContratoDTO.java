
package org.caixa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratoDTO(
        @NotBlank(message = "O número do contrato é obrigatório")
        @Size(max = 30, message = "O número deve ter no máximo 30 caracteres")
        String numero,

        @NotBlank(message = "O nome do contratante é obrigatório")
        @Size(max = 150)
        String contratante,

        @NotNull(message = "O valor é obrigatório")
        @Positive(message = "O valor deve ser maior que zero")
        BigDecimal valor,

        @NotNull(message = "A data de início é obrigatória")
        LocalDate dataInicio
) {}
