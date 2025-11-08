package br.com.challenge.infrastructure.api.rest.dto.output;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "DTO de resposta para usuário")
public class UsuarioOutputDto {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome completo", example = "João Silva Santos")
    private String nomeCompleto;

    @Schema(description = "Idade", example = "30")
    private Integer idade;

    @Schema(description = "CPF", example = "12345678901")
    private String cpf;

    @Schema(description = "CEP", example = "12345678")
    private String cep;

    @Schema(description = "Número do endereço", example = "123")
    private Integer numero;

    @Schema(description = "Complemento", example = "Apto 101")
    private String complemento;

    @Schema(description = "Telefone", example = "11999999999")
    private String telefone;

    @Schema(description = "Versão para controle de concorrência", example = "1")
    private Integer versao;

    public UsuarioOutputDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Integer getVersao() { return versao; }
    public void setVersao(Integer versao) { this.versao = versao; }
}
