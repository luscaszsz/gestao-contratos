package org.caixa.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.caixa.service.ConsultaContratoService;

@Path("/hello")
public class GreetingResource {

    @Inject
    ConsultaContratoService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String hello(@PathParam("id") int id) {
        return service.consultaContrato(id).toString();
    }
}
