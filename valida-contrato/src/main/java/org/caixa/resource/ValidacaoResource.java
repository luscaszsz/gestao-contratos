package org.caixa.resource;

import org.caixa.dto.ContratoDTO;
import org.caixa.dto.PublicacaoRequest;
import org.caixa.dto.ValidacaoResponse;
import org.caixa.service.ValidacaoService;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/contratos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ValidacaoResource {

    @Inject
    ValidacaoService validacaoService;

    @POST
    @Path("/validar")
    public ValidacaoResponse validar(PublicacaoRequest contrato) {
        boolean valido = validacaoService.validar(contrato);
        return new ValidacaoResponse(valido);
    }
}