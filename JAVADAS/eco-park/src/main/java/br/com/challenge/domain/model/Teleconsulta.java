package br.com.challenge.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "teleconsulta")
public class Teleconsulta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_teleconsulta")
    @SequenceGenerator(name = "seq_teleconsulta", sequenceName = "seq_teleconsulta", allocationSize = 1)
    private Long id;

    @Version
    private Long versao = 0L;

    @Column(name = "paciente_id", nullable = false)
    @NotNull(message = "ID do paciente é obrigatório")
    private Long pacienteId;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Médico é obrigatório")
    @Size(min = 3, max = 100, message = "Médico deve ter entre 3 e 100 caracteres")
    private String medico;

    @Column(name = "data_hora", nullable = false)
    @NotNull(message = "Data e hora são obrigatórias")
    @Future(message = "Data e hora devem ser no futuro")
    private LocalDateTime dataHora;

    @Column(length = 20)
    @NotBlank(message = "Status é obrigatório")
    @Pattern(regexp = "AGENDADA|REALIZADA|CANCELADA", message = "Status deve ser AGENDADA, REALIZADA ou CANCELADA")
    private String status = "AGENDADA";

    @Column(length = 500)
    @Size(max = 500, message = "Observações deve ter no máximo 500 caracteres")
    private String observacoes;

    public Teleconsulta() {}

    public Teleconsulta(Long id, Long pacienteId, String medico, LocalDateTime dataHora, String status, String observacoes) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.medico = medico;
        this.dataHora = dataHora;
        this.status = status;
        this.observacoes = observacoes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }

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

    public void incrementarVersao() {
        this.versao = Objects.requireNonNullElse(versao, 0L) + 1;
    }

    public boolean podeSerCancelada() {
        return "AGENDADA".equals(this.status) &&
                this.dataHora != null &&
                this.dataHora.isAfter(LocalDateTime.now().plusHours(2));
    }

    public boolean foiRealizada() {
        return "REALIZADA".equals(this.status);
    }

    public boolean estaAgendada() {
        return "AGENDADA".equals(this.status);
    }

    @Override
    public String toString() {
        return "Teleconsulta{id=" + id + ", medico='" + medico + "', dataHora=" + dataHora +
                ", status='" + status + "', versao=" + versao + "}";
    }
}