package org.caixa.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.caixa.service.ConsultaContratoService;

@Path("/hello")
public class GreetingResource {

    @Inject
    ConsultaContratoService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String hello() {
        return service.consultaContrato().toString();
    }
}
