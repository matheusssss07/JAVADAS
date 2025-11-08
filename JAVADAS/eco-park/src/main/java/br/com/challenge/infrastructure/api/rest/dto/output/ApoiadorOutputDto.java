package br.com.challenge.infrastructure.api.rest.dto.output;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "DTO de resposta para apoiador")
public class ApoiadorOutputDto extends UsuarioOutputDto {

    @Schema(description = "Cargo do apoiador", example = "Enfermeiro")
    private String cargo;

    @Schema(description = "Área de atuação", example = "Enfermagem Geriátrica")
    private String areaAtuacao;

    @Schema(description = "Quantidade de pacientes vinculados", example = "5")
    private Integer quantidadePacientes;

    public ApoiadorOutputDto() {}

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getAreaAtuacao() { return areaAtuacao; }
    public void setAreaAtuacao(String areaAtuacao) { this.areaAtuacao = areaAtuacao; }

    public Integer getQuantidadePacientes() { return quantidadePacientes; }
    public void setQuantidadePacientes(Integer quantidadePacientes) { this.quantidadePacientes = quantidadePacientes; }
}