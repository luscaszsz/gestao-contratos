package org.caixa.resource;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.caixa.dto.PublicacaoRequest;
import org.caixa.dto.PublicacaoResponse;
import org.caixa.dto.PublicacaoStatusResponse;
import org.caixa.service.PublicaContratoService;

@Path("/v1/publicacoes")
public class PublicacaoResource {

    @Inject
    PublicaContratoService service;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public PublicacaoResponse solicitarPublicacao(
            @HeaderParam("Idempotency-Key") String idempotencyKey,
            PublicacaoRequest request) {
        Log.info("Chegou no end /hello/publica");
        return service.solicitarPublicacao(request, idempotencyKey);
    }

    @GET
    @Path("{id}/status")
    public PublicacaoStatusResponse consultarStatus(@PathParam("id") String id){
        return service.consultarStatus(id);
    }
}
