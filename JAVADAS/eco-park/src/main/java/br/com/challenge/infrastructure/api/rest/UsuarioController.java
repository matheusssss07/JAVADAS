package br.com.challenge.infrastructure.api.rest;

import br.com.challenge.application.service.UsuarioApplicationService;
import br.com.challenge.infrastructure.api.rest.dto.input.UsuarioInputDto;
import br.com.challenge.infrastructure.api.rest.dto.output.UsuarioOutputDto;
import br.com.challenge.infrastructure.api.rest.dto.request.LoginRequest;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioController {

    @Inject
    UsuarioApplicationService usuarioApplicationService;

    @Inject
    ModelMapper modelMapper;

    @GET
    public Response listarTodos() {
        try {
            List<UsuarioOutputDto> usuarios = usuarioApplicationService.listarTodos().stream()
                    .map(usuario -> modelMapper.map(usuario, UsuarioOutputDto.class))
                    .collect(Collectors.toList());
            return Response.ok(usuarios).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao listar usuários: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") Long id) {
        try {
            var usuario = usuarioApplicationService.buscarPorId(id);
            var response = modelMapper.map(usuario, UsuarioOutputDto.class);
            return Response.ok(response).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GET
    @Path("/cpf/{cpf}")
    public Response buscarPorCpf(@PathParam("cpf") String cpf) {
        try {
            var usuario = usuarioApplicationService.buscarPorCpf(cpf);
            var response = modelMapper.map(usuario, UsuarioOutputDto.class);
            return Response.ok(response).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @POST
    public Response cadastrar(@Valid UsuarioInputDto usuarioInputDto) {
        try {
            var usuario = modelMapper.map(usuarioInputDto, br.com.challenge.domain.model.Usuario.class);
            var usuarioSalvo = usuarioApplicationService.cadastrar(usuario);
            var response = modelMapper.map(usuarioSalvo, UsuarioOutputDto.class);

            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") Long id, @Valid UsuarioInputDto usuarioInputDto) {
        try {
            var usuario = modelMapper.map(usuarioInputDto, br.com.challenge.domain.model.Usuario.class);
            var usuarioAtualizado = usuarioApplicationService.atualizar(id, usuario);
            var response = modelMapper.map(usuarioAtualizado, UsuarioOutputDto.class);

            return Response.ok(response).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") Long id) {
        try {
            usuarioApplicationService.deletar(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest loginRequest) {
        try {
            boolean loginValido = usuarioApplicationService.validarLogin(
                    loginRequest.getCpf(), loginRequest.getSenha());

            if (loginValido) {
                return Response.ok(Map.of(
                        "message", "Login realizado com sucesso",
                        "authenticated", true
                )).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of(
                                "error", "CPF ou senha inválidos",
                                "authenticated", false
                        ))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao realizar login: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/estatisticas/total")
    public Response contarTotal() {
        try {
            int total = usuarioApplicationService.contarTotalUsuarios();
            return Response.ok(Map.of("total", total)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao contar usuários: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/verificar-cpf/{cpf}")
    public Response verificarCpf(@PathParam("cpf") String cpf) {
        try {
            boolean disponivel = usuarioApplicationService.verificarDisponibilidadeCpf(cpf);
            return Response.ok(Map.of("disponivel", disponivel)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Erro ao verificar CPF: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/health")
    public Response healthCheck() {
        return Response.ok(Map.of("status", "UP", "servico", "usuarios")).build();
    }

    @GET
    @Path("/meu-nome/{name}")
    public Response testeName(@PathParam("name") String name) {
        return Response.ok(Map.of("mensagem", "Bem-vindo(a) " + name)).build();
    }

    private Response handleException(Exception e) {
        Map<String, String> errorResponse = Map.of("error", e.getMessage());

        if (e.getMessage().contains("não encontrado") || e.getMessage().contains("not found")) {
            return Response.status(Response.Status.NOT_FOUND).entity(errorResponse).build();
        } else if (e.getMessage().contains("inválido") || e.getMessage().contains("já cadastrado")) {
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
        }
    }
}