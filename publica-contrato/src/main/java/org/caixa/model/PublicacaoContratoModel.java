
package org.caixa.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "publicacao-contrato")
public class PublicacaoContratoModel extends PanacheEntityBase {

    @Id
    public UUID id;

    @Column(name = "nome_funcao", nullable = false, length = 130)
    public String nomeFuncao;

    @Column(name = "periodicidade", nullable = false, length = 130)
    public String periodicidade;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    public BigDecimal valor;

    @Column(name = "versao", nullable = false)
    public Integer versao;

    @Column(name = "data_inicio", nullable = false)
    public LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    public LocalDate dataFim;

    @Column(name = "status_publicacao", nullable = false, length = 20)
    public String statusPublicacao;

    @Column(name = "status_validacao", nullable = false, length = 20)
    public String statusValidacao;

    @Column(name = "motivo", length = 120)
    public String motivo;

    @Column(name = "ultima_atualizacao", nullable = false)
    public OffsetDateTime ultimaAtualizacao;

}
