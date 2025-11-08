package br.com.challenge.infrastructure.api.rest.dto.output;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "DTO de resposta para paciente")
public class PacienteOutputDto extends UsuarioOutputDto {

    @Schema(description = "Telefone de contato adicional", example = "11888888888")
    private String telefoneContato;

    @Schema(description = "Número do SUS ou Convênio", example = "123456789012345")
    private String numeroSusOuConvenio;

    @Schema(description = "ID do apoiador vinculado", example = "1")
    private Long apoiadorId;

    @Schema(description = "Nome do apoiador vinculado", example = "Maria Santos")
    private String nomeApoiador;

    public PacienteOutputDto() {}

    public String getTelefoneContato() {
        return telefoneContato;
    }

    public void setTelefoneContato(String telefoneContato) {
        this.telefoneContato = telefoneContato;
    }

    public String getNumeroSusOuConvenio() {
        return numeroSusOuConvenio;
    }

    public void setNumeroSusOuConvenio(String numeroSusOuConvenio) {
        this.numeroSusOuConvenio = numeroSusOuConvenio;
    }

    public Long getApoiadorId() {
        return apoiadorId;
    }

    public void setApoiadorId(Long apoiadorId) {
        this.apoiadorId = apoiadorId;
    }

    public String getNomeApoiador() {
        return nomeApoiador;
    }

    public void setNomeApoiador(String nomeApoiador) {
        this.nomeApoiador = nomeApoiador;
    }
}