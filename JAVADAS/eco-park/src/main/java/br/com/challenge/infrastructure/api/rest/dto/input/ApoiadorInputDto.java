package br.com.challenge.infrastructure.api.rest.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "DTO para cadastro e atualização de apoiador")
public class ApoiadorInputDto extends UsuarioInputDto {

    @NotBlank(message = "Cargo é obrigatório")
    @Size(min = 2, max = 50, message = "Cargo deve ter entre 2 e 50 caracteres")
    @Schema(description = "Cargo do apoiador", example = "Enfermeiro")
    private String cargo;

    @NotBlank(message = "Área de atuação é obrigatória")
    @Size(min = 2, max = 100, message = "Área de atuação deve ter entre 2 e 100 caracteres")
    @Schema(description = "Área de atuação do apoiador", example = "Enfermagem Geriátrica")
    private String areaAtuacao;

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getAreaAtuacao() { return areaAtuacao; }
    public void setAreaAtuacao(String areaAtuacao) { this.areaAtuacao = areaAtuacao; }
}