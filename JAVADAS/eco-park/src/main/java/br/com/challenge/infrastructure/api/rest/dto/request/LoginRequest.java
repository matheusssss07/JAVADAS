package br.com.challenge.infrastructure.api.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "DTO para autenticação de usuário")
public class LoginRequest {

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    @Schema(description = "CPF do usuário", example = "12345678901")
    private String cpf;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha do usuário", example = "senha123")
    private String senha;

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}