package br.com.challenge.infrastructure.api.rest;

import br.com.challenge.application.service.TeleconsultaApplicationService;
import br.com.challenge.infrastructure.api.rest.dto.input.TeleconsultaInputDto;
import br.com.challenge.infrastructure.api.rest.dto.output.TeleconsultaOutputDto;
import br.com.challenge.infrastructure.api.rest.dto.request.StatusUpdateRequest;
import br.com.challenge.infrastructure.api.rest.dto.request.ObservacoesRequest;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/teleconsultas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TeleconsultaController {

    @Inject
    TeleconsultaApplicationService teleconsultaApplicationService;

    @Inject
    ModelMapper modelMapper;

    @GET
    public Response listarTodas() {
        try {
            List<TeleconsultaOutputDto> teleconsultas = teleconsultaApplicationService.listarTodas().stream()
                    .map(teleconsulta -> modelMapper.map(teleconsulta, TeleconsultaOutputDto.class))
                    .collect(Collectors.toList());
            return Response.ok(teleconsultas).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao listar teleconsultas: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            var teleconsulta = teleconsultaApplicationService.buscarPorId(id);
            var response = modelMapper.map(teleconsulta, TeleconsultaOutputDto.class);
            return Response.ok(response).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @POST
    public Response agendar(@Valid TeleconsultaInputDto teleconsultaInputDto) {
        try {
            var teleconsulta = modelMapper.map(teleconsultaInputDto, br.com.challenge.domain.model.Teleconsulta.class);
            var teleconsultaAgendada = teleconsultaApplicationService.agendar(teleconsulta);
            var response = modelMapper.map(teleconsultaAgendada, TeleconsultaOutputDto.class);

            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, @Valid TeleconsultaInputDto teleconsultaInputDto) {
        try {
            var teleconsulta = modelMapper.map(teleconsultaInputDto, br.com.challenge.domain.model.Teleconsulta.class);
            var teleconsultaAtualizada = teleconsultaApplicationService.atualizar(id, teleconsulta);
            var response = modelMapper.map(teleconsultaAtualizada, TeleconsultaOutputDto.class);

            return Response.ok(response).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") Long id) {
        try {
            teleconsultaApplicationService.deletar(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @POST
    @Path("/{id}/cancelar")
    public Response cancelar(@PathParam("id") Long id) {
        try {
            teleconsultaApplicationService.cancelar(id);
            return Response.ok(Map.of("message", "Teleconsulta cancelada com sucesso")).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/paciente/{pacienteId}")
    public Response buscarPorPaciente(@PathParam("pacienteId") Long pacienteId) {
        try {
            List<TeleconsultaOutputDto> teleconsultas = teleconsultaApplicationService.buscarPorPaciente(pacienteId).stream()
                    .map(teleconsulta -> modelMapper.map(teleconsulta, TeleconsultaOutputDto.class))
                    .collect(Collectors.toList());
            return Response.ok(teleconsultas).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/medico/{medico}")
    public Response buscarPorMedico(@PathParam("medico") String medico) {
        try {
            List<TeleconsultaOutputDto> teleconsultas = teleconsultaApplicationService.buscarPorMedico(medico).stream()
                    .map(teleconsulta -> modelMapper.map(teleconsulta, TeleconsultaOutputDto.class))
                    .collect(Collectors.toList());
            return Response.ok(teleconsultas).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/status/{status}")
    public Response buscarPorStatus(@PathParam("status") String status) {
        try {
            List<TeleconsultaOutputDto> teleconsultas = teleconsultaApplicationService.buscarPorStatus(status).stream()
                    .map(teleconsulta -> modelMapper.map(teleconsulta, TeleconsultaOutputDto.class))
                    .collect(Collectors.toList());
            return Response.ok(teleconsultas).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/hoje")
    public Response consultasDeHoje() {
        try {
            List<TeleconsultaOutputDto> teleconsultas = teleconsultaApplicationService.consultasDeHoje().stream()
                    .map(teleconsulta -> modelMapper.map(teleconsulta, TeleconsultaOutputDto.class))
                    .collect(Collectors.toList());
            return Response.ok(teleconsultas).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao buscar consultas de hoje: " + e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}/status")
    public Response atualizarStatus(@PathParam("id") Long id, @Valid StatusUpdateRequest statusRequest) {
        try {
            teleconsultaApplicationService.atualizarStatus(id, statusRequest.getStatus());
            return Response.ok(Map.of("message", "Status atualizado com sucesso")).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PUT
    @Path("/{id}/observacoes")
    public Response adicionarObservacoes(@PathParam("id") Long id, @Valid ObservacoesRequest observacoesRequest) {
        try {
            teleconsultaApplicationService.adicionarObservacoes(id, observacoesRequest.getObservacoes());
            return Response.ok(Map.of("message", "Observações adicionadas com sucesso")).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/disponibilidade")
    public Response verificarDisponibilidade(@QueryParam("dataHora") String dataHoraStr,
                                             @QueryParam("medico") String medico) {
        try {
            LocalDateTime dataHora = LocalDateTime.parse(dataHoraStr);
            boolean disponivel = teleconsultaApplicationService.verificarDisponibilidade(dataHora, medico);
            return Response.ok(Map.of("disponivel", disponivel)).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/estatisticas/total")
    public Response contarTotal() {
        try {
            int total = teleconsultaApplicationService.contarTotal();
            return Response.ok(Map.of("total", total)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao contar teleconsultas: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/estatisticas/status/{status}")
    public Response contarPorStatus(@PathParam("status") String status) {
        try {
            int count = teleconsultaApplicationService.contarPorStatus(status);
            return Response.ok(Map.of("count", count)).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/health")
    public Response healthCheck() {
        return Response.ok(Map.of("status", "UP", "servico", "teleconsultas")).build();
    }

    private Response handleException(Exception e) {
        Map<String, String> errorResponse = Map.of("error", e.getMessage());

        if (e.getMessage().contains("não encontrado") || e.getMessage().contains("not found")) {
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        } else if (e.getMessage().contains("inválido") || e.getMessage().contains("disponível") ||
                e.getMessage().contains("futuro") || e.getMessage().contains("Status inválido")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }
}