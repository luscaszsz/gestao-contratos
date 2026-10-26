package org.caixa.service;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.RequestScoped;
import org.caixa.model.ContratoModel;



@RequestScoped
public class ConsultaContratoService {

    @CacheResult(cacheName = "contrato-por-id")
    public ContratoModel consultaContrato(@CacheKey int id) throws InterruptedException {

        Thread.sleep(10_000);
        return ContratoModel.findById(id);
    }
}
