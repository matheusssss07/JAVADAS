package br.com.challenge.infrastructure.api.rest.dto.input;

import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "DTO para cadastro e atualização de usuário")
public class UsuarioInputDto {

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Schema(description = "Nome completo do usuário", example = "João Silva Santos")
    private String nomeCompleto;

    @NotNull(message = "Idade é obrigatória")
    @Min(value = 0, message = "Idade não pode ser negativa")
    @Max(value = 150, message = "Idade não pode ser maior que 150")
    @Schema(description = "Idade do usuário", example = "30")
    private Integer idade;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    @Schema(description = "CPF do usuário (apenas números)", example = "12345678901")
    private String cpf;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    @Schema(description = "CEP do endereço", example = "12345678")
    private String cep;

    @NotNull(message = "Número é obrigatório")
    @Min(value = 1, message = "Número deve ser maior que 0")
    @Schema(description = "Número do endereço", example = "123")
    private Integer numero;

    @Schema(description = "Complemento do endereço", example = "Apto 101")
    private String complemento;

    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos")
    @Schema(description = "Telefone de contato", example = "11999999999")
    private String telefone;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    @Schema(description = "Senha do usuário", example = "senha123")
    private String senha;

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}