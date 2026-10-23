package org.caixa.service;

import jakarta.enterprise.context.RequestScoped;
import org.caixa.model.ContratoModel;


@RequestScoped
public class ConsultaContratoService {

    public ContratoModel consultaContrato(){

        return ContratoModel.findById(1);
    }
}
