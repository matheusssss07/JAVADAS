package br.com.challenge.infrastructure.api.rest;

import br.com.challenge.application.service.ApoiadorApplicationService;
import br.com.challenge.infrastructure.api.rest.dto.input.ApoiadorInputDto;
import br.com.challenge.infrastructure.api.rest.dto.output.ApoiadorOutputDto;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/apoiadores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApoiadorController {

    @Inject
    ApoiadorApplicationService apoiadorApplicationService;

    @Inject
    ModelMapper modelMapper;

    @GET
    public Response listarTodos() {
        try {
            List<ApoiadorOutputDto> apoiadores = apoiadorApplicationService.listarTodos().stream()
                    .map(apoiador -> modelMapper.map(apoiador, ApoiadorOutputDto.class))
                    .collect(Collectors.toList());
            return Response.ok(apoiadores).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao listar apoiadores: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            var apoiador = apoiadorApplicationService.buscarPorId(id);
            var response = modelMapper.map(apoiador, ApoiadorOutputDto.class);
            return Response.ok(response).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/cpf/{cpf}")
    public Response buscarPorCpf(@PathParam("cpf") String cpf) {
        try {
            var apoiador = apoiadorApplicationService.buscarPorCpf(cpf);
            var response = modelMapper.map(apoiador, ApoiadorOutputDto.class);
            return Response.ok(response).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    //@POST
    //public Response cadastrar(@Valid ApoiadorInputDto apoiadorInputDto) {
    //    try {
    //        var apoiador = modelMapper.map(apoiadorInputDto, br.com.challenge.domain.model.Apoiador.class);
    //        var apoiadorSalvo = apoiadorApplicationService.cadastrar(apoiador);
    //        var response = modelMapper.map(apoiadorSalvo, ApoiadorOutputDto.class);
//
    //        return Response.status(Response.Status.CREATED)
    //                .entity(response)
    //                .build();
    //    } catch (Exception e) {
    //        return handleException(e);
    //    }
    //}

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, @Valid ApoiadorInputDto apoiadorInputDto) {
        try {
            var apoiador = modelMapper.map(apoiadorInputDto, br.com.challenge.domain.model.Apoiador.class);
            var apoiadorAtualizado = apoiadorApplicationService.atualizar(id, apoiador);
            var response = modelMapper.map(apoiadorAtualizado, ApoiadorOutputDto.class);

            return Response.ok(response).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") Long id) {
        try {
            apoiadorApplicationService.deletar(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/cargo/{cargo}")
    public Response buscarPorCargo(@PathParam("cargo") String cargo) {
        try {
            List<ApoiadorOutputDto> apoiadores = apoiadorApplicationService.buscarPorCargo(cargo).stream()
                    .map(apoiador -> modelMapper.map(apoiador, ApoiadorOutputDto.class))
                    .collect(Collectors.toList());
            return Response.ok(apoiadores).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/cargos")
    public Response listarCargos() {
        try {
            List<String> cargos = apoiadorApplicationService.listarCargos();
            return Response.ok(cargos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao listar cargos: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/{apoiadorId}/vincular-paciente/{pacienteId}")
    public Response vincularPaciente(@PathParam("apoiadorId") Long apoiadorId,
                                     @PathParam("pacienteId") Long pacienteId) {
        try {
            apoiadorApplicationService.vincularPaciente(apoiadorId, pacienteId);
            return Response.ok(Map.of("message", "Paciente vinculado com sucesso")).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @POST
    @Path("/desvincular-paciente/{pacienteId}")
    public Response desvincularPaciente(@PathParam("pacienteId") Long pacienteId) {
        try {
            apoiadorApplicationService.desvincularPaciente(pacienteId);
            return Response.ok(Map.of("message", "Paciente desvinculado com sucesso")).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/estatisticas/total")
    public Response contarTotal() {
        try {
            int total = apoiadorApplicationService.contarTotalApoiadores();
            return Response.ok(Map.of("total", total)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao contar apoiadores: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}/pacientes-vinculados/count")
    public Response contarPacientesVinculados(@PathParam("id") Long id) {
        try {
            int count = apoiadorApplicationService.contarPacientesVinculados(id);
            return Response.ok(Map.of("count", count)).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private Response handleException(Exception e) {
        Map<String, String> errorResponse = Map.of("error", e.getMessage());

        if (e.getMessage().contains("não encontrado") || e.getMessage().contains("not found")) {
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        } else if (e.getMessage().contains("já cadastrado") || e.getMessage().contains("inválido") ||
                e.getMessage().contains("vinculados")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }































    @POST
    public Response cadastrar(@Valid ApoiadorInputDto apoiadorInputDto) {
        try {
            // DEBUG
            System.out.println("=== DEBUG APOIADOR CONTROLLER ===");
            System.out.println("DTO CPF: " + apoiadorInputDto.getCpf());
            System.out.println("DTO Nome: " + apoiadorInputDto.getNomeCompleto());
            System.out.println("DTO Cargo: " + apoiadorInputDto.getCargo());

            var apoiador = modelMapper.map(apoiadorInputDto, br.com.challenge.domain.model.Apoiador.class);

            // DEBUG
            System.out.println("Entidade CPF: " + apoiador.getCpf());
            System.out.println("Entidade Nome: " + apoiador.getNomeCompleto());
            System.out.println("Entidade Cargo: " + apoiador.getCargo());

            var apoiadorSalvo = apoiadorApplicationService.cadastrar(apoiador);
            var response = modelMapper.map(apoiadorSalvo, ApoiadorOutputDto.class);

            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (Exception e) {
            System.out.println("=== ERRO NO APOIADOR CONTROLLER ===");
            e.printStackTrace();
            return handleException(e);
        }
    }




}