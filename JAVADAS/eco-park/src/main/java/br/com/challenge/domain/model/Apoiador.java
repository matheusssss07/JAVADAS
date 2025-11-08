package br.com.challenge.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "apoiador")
@DiscriminatorValue("APOIADOR")
public class Apoiador extends Usuario {

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Cargo é obrigatório")
    @Size(min = 2, max = 50, message = "Cargo deve ter entre 2 e 50 caracteres")
    private String cargo;

    @Column(name = "area_atuacao", length = 100)
    @NotBlank(message = "Área de atuação é obrigatória")
    @Size(min = 2, max = 100, message = "Área de atuação deve ter entre 2 e 100 caracteres")
    private String areaAtuacao;

    public Apoiador() {}

    public Apoiador(Long id, String nomeCompleto, Integer idade, String cpf, String cep,
                    Integer numero, String complemento, String telefone, String senha,
                    String cargo, String areaAtuacao, Long versao) {
        super(id, nomeCompleto, idade, cpf, cep, numero, complemento, telefone, senha, versao);
        this.cargo = cargo;
        this.areaAtuacao = areaAtuacao;
    }

    public Apoiador(long id, String nomeCompleto, int idade, String cpf, String cep,
                    int numero, String complemento, String telefone, String senha,
                    String cargo, String areaAtuacao) {
        super(id, nomeCompleto, idade, cpf, cep, numero, complemento, telefone, senha);
        this.cargo = cargo;
        this.areaAtuacao = areaAtuacao;
    }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getAreaAtuacao() { return areaAtuacao; }
    public void setAreaAtuacao(String areaAtuacao) { this.areaAtuacao = areaAtuacao; }

    public boolean podeSerDeletado() {
        return true;
    }

    @Override
    public String toString() {
        return "Apoiador{id=" + getId() + ", nome='" + getNomeCompleto() + "', cargo='" + cargo + "', versao=" + getVersao() + "}";
    }
}