package br.com.challenge.infrastructure.api.rest.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "DTO para cadastro e atualização de paciente")
public class PacienteInputDto extends UsuarioInputDto {

    @NotBlank(message = "Telefone de contato é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone de contato deve conter 10 ou 11 dígitos")
    @Schema(description = "Telefone de contato adicional", example = "11888888888")
    private String telefoneContato;

    @Schema(description = "Número do SUS ou Convênio", example = "123456789012345")
    private String numeroSusOuConvenio;

    @Schema(description = "ID do apoiador vinculado (opcional)", example = "1")
    private Long apoiadorId;

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
}