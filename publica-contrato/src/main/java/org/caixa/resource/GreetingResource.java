package org.caixa.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jdk.jfr.RecordingState;
import org.caixa.dto.PublicacaoRequest;
import org.caixa.service.PublicaContratoService;

@Path("/hello")
public class GreetingResource {

    @Inject
    PublicaContratoService service;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/publica")
    public Response hello(PublicacaoRequest request) {
        Integer inteiro = service.publicar(request);
        return Response.ok().entity(inteiro).build();
    }
}
