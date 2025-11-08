package br.com.challenge.infrastructure.api.rest.dto.output;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO de resposta para teleconsulta")
public class TeleconsultaOutputDto {

    @Schema(description = "ID da teleconsulta", example = "1")
    private Long id;

    @Schema(description = "ID do paciente", example = "1")
    private Long pacienteId;

    @Schema(description = "Nome do paciente", example = "João Silva")
    private String nomePaciente;

    @Schema(description = "Nome do médico", example = "Dr. Carlos Alberto")
    private String medico;

    @Schema(description = "Data e hora da consulta", example = "2024-01-15T14:30:00")
    private LocalDateTime dataHora;

    @Schema(description = "Status da consulta", example = "AGENDADA")
    private String status;

    @Schema(description = "Observações da consulta", example = "Paciente com hipertensão controlada")
    private String observacoes;

    @Schema(description = "Versão para controle de concorrência", example = "1")
    private Integer versao;

    public TeleconsultaOutputDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

    public String getNomePaciente() { return nomePaciente; }
    public void setNomePaciente(String nomePaciente) { this.nomePaciente = nomePaciente; }

    public String getMedico() { return medico; }
    public void setMedico(String medico) { this.medico = medico; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Integer getVersao() { return versao; }
    public void setVersao(Integer versao) { this.versao = versao; }
}