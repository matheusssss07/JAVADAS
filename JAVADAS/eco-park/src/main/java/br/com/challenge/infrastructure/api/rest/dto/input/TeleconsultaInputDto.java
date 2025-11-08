package br.com.challenge.infrastructure.api.rest.dto.input;

import jakarta.validation.constraints.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO para agendamento e atualização de teleconsulta")
public class TeleconsultaInputDto {

    @NotNull(message = "ID do paciente é obrigatório")
    @Schema(description = "ID do paciente", example = "1")
    private Long pacienteId;

    @NotBlank(message = "Nome do médico é obrigatório")
    @Size(min = 3, max = 100, message = "Nome do médico deve ter entre 3 e 100 caracteres")
    @Schema(description = "Nome do médico", example = "Dr. Carlos Alberto")
    private String medico;

    @NotNull(message = "Data e hora são obrigatórias")
    @Future(message = "Data e hora devem ser futuras")
    @Schema(description = "Data e hora da consulta", example = "2024-01-15T14:30:00")
    private LocalDateTime dataHora;

    @NotBlank(message = "Status é obrigatório")
    @Pattern(regexp = "AGENDADA|CONCLUIDA|CANCELADA", message = "Status deve ser AGENDADA, CONCLUIDA ou CANCELADA")
    @Schema(description = "Status da consulta", example = "AGENDADA")
    private String status;

    @Schema(description = "Observações da consulta", example = "Paciente com hipertensão controlada")
    private String observacoes;

    // Getters e Setters
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public String getMedico() { return medico; }
    public void setMedico(String medico) { this.medico = medico; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}