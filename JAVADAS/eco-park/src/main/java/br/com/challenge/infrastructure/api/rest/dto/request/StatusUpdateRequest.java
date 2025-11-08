package br.com.challenge.infrastructure.api.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "DTO para atualização de status")
public class StatusUpdateRequest {

    @NotBlank(message = "Status é obrigatório")
    @Pattern(regexp = "AGENDADA|CONCLUIDA|CANCELADA", message = "Status deve ser AGENDADA, CONCLUIDA ou CANCELADA")
    @Schema(description = "Novo status", example = "CONCLUIDA")
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}