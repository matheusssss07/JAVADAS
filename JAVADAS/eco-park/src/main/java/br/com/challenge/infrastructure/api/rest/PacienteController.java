package br.com.challenge.infrastructure.api.rest;

import br.com.challenge.application.service.PacienteApplicationService;
import br.com.challenge.infrastructure.api.rest.dto.input.PacienteInputDto;
import br.com.challenge.infrastructure.api.rest.dto.output.PacienteOutputDto;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/pacientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacienteController {

    @Inject
    PacienteApplicationService pacienteApplicationService;

    @Inject
    ModelMapper modelMapper;

    @GET
    public Response listarTodos() {
        try {
            List<PacienteOutputDto> pacientes = pacienteApplicationService.listarTodos().stream()
                    .map(paciente -> modelMapper.map(paciente, PacienteOutputDto.class))
                    .collect(Collectors.toList());
            return Response.ok(pacientes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao listar pacientes: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            var paciente = pacienteApplicationService.buscarPorId(id);
            var response = modelMapper.map(paciente, PacienteOutputDto.class);
            return Response.ok(response).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/cpf/{cpf}")
    public Response buscarPorCpf(@PathParam("cpf") String cpf) {
        try {
            var paciente = pacienteApplicationService.buscarPorCpf(cpf);
            var response = modelMapper.map(paciente, PacienteOutputDto.class);
            return Response.ok(response).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    //@POST
    //public Response cadastrar(@Valid PacienteInputDto pacienteInputDto) {
    //    try {
    //        var paciente = modelMapper.map(pacienteInputDto, br.com.challenge.domain.model.Paciente.class);
    //        var pacienteSalvo = pacienteApplicationService.cadastrar(paciente);
    //        var response = modelMapper.map(pacienteSalvo, PacienteOutputDto.class);
//
 //            return Response.status(Response.Status.CREATED)
 //                   .entity(response)
 //                   .build();
  //      } catch (Exception e) {
    //        return handleException(e);
    //    }
    //}

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, @Valid PacienteInputDto pacienteInputDto) {
        try {
            var paciente = modelMapper.map(pacienteInputDto, br.com.challenge.domain.model.Paciente.class);
            var pacienteAtualizado = pacienteApplicationService.atualizar(id, paciente);
            var response = modelMapper.map(pacienteAtualizado, PacienteOutputDto.class);

            return Response.ok(response).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") Long id) {
        try {
            pacienteApplicationService.deletar(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/apoiador/{apoiadorId}")
    public Response buscarPorApoiador(@PathParam("apoiadorId") Long apoiadorId) {
        try {
            List<PacienteOutputDto> pacientes = pacienteApplicationService.buscarPorApoiador(apoiadorId).stream()
                    .map(paciente -> modelMapper.map(paciente, PacienteOutputDto.class))
                    .collect(Collectors.toList());
            return Response.ok(pacientes).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @POST
    @Path("/{pacienteId}/vincular/{apoiadorId}")
    public Response vincularApoiador(@PathParam("pacienteId") Long pacienteId,
                                     @PathParam("apoiadorId") Long apoiadorId) {
        try {
            pacienteApplicationService.vincularApoiador(pacienteId, apoiadorId);
            return Response.ok(Map.of("message", "Paciente vinculado ao apoiador com sucesso")).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @POST
    @Path("/{pacienteId}/desvincular")
    public Response desvincularApoiador(@PathParam("pacienteId") Long pacienteId) {
        try {
            pacienteApplicationService.desvincularApoiador(pacienteId);
            return Response.ok(Map.of("message", "Paciente desvinculado com sucesso")).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/estatisticas/total")
    public Response contarTotal() {
        try {
            int total = pacienteApplicationService.contarTotalPacientes();
            return Response.ok(Map.of("total", total)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao contar pacientes: " + e.getMessage()))
                    .build();
        }
    }

    private Response handleException(Exception e) {
        Map<String, String> errorResponse = Map.of("error", e.getMessage());

        if (e.getMessage().contains("não encontrado") || e.getMessage().contains("not found")) {
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        } else if (e.getMessage().contains("já cadastrado") || e.getMessage().contains("inválido")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }







    @POST
    public Response cadastrar(@Valid PacienteInputDto pacienteInputDto) {
        try {
            // DEBUG - Verificar se o DTO está chegando corretamente
            System.out.println("=== DEBUG PACIENTE CONTROLLER ===");
            System.out.println("DTO CPF: " + pacienteInputDto.getCpf());
            System.out.println("DTO Nome: " + pacienteInputDto.getNomeCompleto());
            System.out.println("DTO Idade: " + pacienteInputDto.getIdade());

            var paciente = modelMapper.map(pacienteInputDto, br.com.challenge.domain.model.Paciente.class);

            // DEBUG - Verificar se o ModelMapper está funcionando
            System.out.println("Entidade CPF: " + paciente.getCpf());
            System.out.println("Entidade Nome: " + paciente.getNomeCompleto());
            System.out.println("Entidade Idade: " + paciente.getIdade());

            var pacienteSalvo = pacienteApplicationService.cadastrar(paciente);
            var response = modelMapper.map(pacienteSalvo, PacienteOutputDto.class);

            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (Exception e) {
            System.out.println("=== ERRO NO PACIENTE CONTROLLER ===");
            e.printStackTrace();
            return handleException(e);
        }
    }




}