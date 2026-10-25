
package org.caixa.resource;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.caixa.service.ConsultaContratoService;

@Path("/hello")
public class GreetingResource {

    @Inject
    ConsultaContratoService service;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    @CacheResult(cacheName = "contrato-por-id")
    public String hello(@PathParam("id") @CacheKey int id)
            throws InterruptedException {

        Log.infof("ENTROU NO RESOURCE: id=%d", id);

        Thread.sleep(10_000);

        Log.info("FINALIZOU A ESPERA DE 10 SEGUNDOS");

        return service.consultaContrato(id).toString();
    }
}
