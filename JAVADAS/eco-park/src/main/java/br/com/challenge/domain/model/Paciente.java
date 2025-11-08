package br.com.challenge.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "paciente")
@DiscriminatorValue("PACIENTE")
public class Paciente extends Usuario {

    @Column(name = "numero_sus_ou_convenio", length = 20)
    private String numeroSusOuConvenio;

    @Column(name = "telefone_contato", length = 15)
    @NotBlank(message = "Telefone de contato é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone de contato deve conter 10 ou 11 dígitos")
    private String telefoneContato;

    @Column(name = "apoiador_id")
    private Long apoiadorId;

    public Paciente() {}

    public Paciente(Long id, String nomeCompleto, Integer idade, String cpf, String cep,
                    Integer numero, String complemento, String telefone, String senha,
                    String telefoneContato, Long apoiadorId, String numeroSusOuConvenio, Long versao) {
        super(id, nomeCompleto, idade, cpf, cep, numero, complemento, telefone, senha, versao);
        this.telefoneContato = telefoneContato;
        this.apoiadorId = apoiadorId;
        this.numeroSusOuConvenio = numeroSusOuConvenio;
    }

    public Paciente(Long id, String nomeCompleto, Integer idade, String cpf, String cep,
                    Integer numero, String complemento, String telefone, String senha,
                    String telefoneContato, Long apoiadorId, String numeroSusOuConvenio) {
        super(id, nomeCompleto, idade, cpf, cep, numero, complemento, telefone, senha);
        this.telefoneContato = telefoneContato;
        this.apoiadorId = apoiadorId;
        this.numeroSusOuConvenio = numeroSusOuConvenio;
    }

    public Paciente(Long id, String nomeCompleto, Integer idade, String cpf, String cep,
                    Integer numero, String complemento, String telefone, String senha,
                    String telefoneContato, Long apoiadorId) {
        super(id, nomeCompleto, idade, cpf, cep, numero, complemento, telefone, senha);
        this.telefoneContato = telefoneContato;
        this.apoiadorId = apoiadorId;
    }

    public String getNumeroSusOuConvenio() {
        return numeroSusOuConvenio;
    }

    public void setNumeroSusOuConvenio(String numeroSusOuConvenio) {
        this.numeroSusOuConvenio = numeroSusOuConvenio;
    }

    public String getTelefoneContato() {
        return telefoneContato;
    }

    public void setTelefoneContato(String telefoneContato) {
        this.telefoneContato = telefoneContato;
    }

    public Long getApoiadorId() {
        return apoiadorId;
    }

    public void setApoiadorId(Long apoiadorId) {
        this.apoiadorId = apoiadorId;
    }

    public boolean temApoiador() {
        return this.apoiadorId != null;
    }

    @Override
    public String toString() {
        return "Paciente{id=" + getId() + ", nome='" + getNomeCompleto() + "', numeroSusOuConvenio='" + numeroSusOuConvenio + "', versao=" + getVersao() + "}";
    }
}