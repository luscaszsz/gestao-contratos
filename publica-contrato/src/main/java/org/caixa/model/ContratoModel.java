
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
import java.util.UUID;

@Entity
@Table(name = "contrato")
public class ContratoModel extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(name = "numero", nullable = false, length = 30)
    public String numero;

    @Column(name = "contratante", nullable = false, length = 150)
    public String contratante;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    public BigDecimal valor;

    @Column(name = "data_inicio", nullable = false)
    public LocalDate dataInicio;

    public static ContratoModel encontraPorId(Integer id){
        return ContratoModel.findById(id);
    }
}
