package org.caixa.resource;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.caixa.service.ConsultaContratoService;

@Path("/v1/consulta")
public class ConsultaResource {

    @Inject
    ConsultaContratoService service;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Blocking
    public Response consultar(@PathParam("id") int id) throws InterruptedException {
        Log.infof("CACHE MISS: simulando lentidão para id=%d", id);

        var contrato = service.consultaContrato(id);
        Log.infof("Consulta lenta concluída para id=%d", id);

        return Response.ok(contrato).build();
    }
}