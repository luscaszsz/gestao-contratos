package org.caixa.restclient;

import org.caixa.dto.PublicacaoRequest;
import org.caixa.dto.ValidacaoResponse;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;

@Path("/contratos")
@RegisterRestClient(configKey = "validacao-api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ValidacaoClient {

    @POST
    @Path("/validar")
    ValidacaoResponse validar(PublicacaoRequest contrato);
}