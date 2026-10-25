package org.caixa.service;

import jakarta.enterprise.context.RequestScoped;
import org.caixa.model.ContratoModel;



@RequestScoped
public class ConsultaContratoService {

    public ContratoModel consultaContrato(int id) {


        return ContratoModel.findById(id);
    }
}
