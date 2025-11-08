package br.com.challenge.infrastructure.api.rest.dto.request;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "DTO para adicionar observações")
public class ObservacoesRequest {

    @Schema(description = "Observações da consulta", example = "Paciente relatou melhora significativa")
    private String observacoes;

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}