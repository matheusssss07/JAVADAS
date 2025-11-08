package br.com.challenge.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Objects;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuario")
    @SequenceGenerator(name = "seq_usuario", sequenceName = "seq_usuario", allocationSize = 1)
    private Long id;

    @Version
    private Long versao = 0L;

    @Column(name = "nome_completo", nullable = false)
    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nomeCompleto;

    @Column(nullable = false)
    @NotNull(message = "Idade é obrigatória")
    @Min(value = 0, message = "Idade deve ser maior ou igual a 0")
    @Max(value = 150, message = "Idade deve ser menor ou igual a 150")
    private Integer idade;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @Column(nullable = false)
    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    private String cep;

    @Column(nullable = false)
    @NotNull(message = "Número é obrigatório")
    @Min(value = 1, message = "Número deve ser maior que 0")
    private Integer numero;

    @Column(length = 100)
    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    private String complemento;

    @Column(length = 15)
    @NotBlank(message = "Telefone é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos")
    private String telefone;

    @Column(nullable = false)
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    public Usuario() {}

    public Usuario(Long id, String nomeCompleto, int idade, String cpf, String cep,
                   int numero, String complemento, String telefone, String senha) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.idade = idade;
        this.cpf = cpf;
        this.cep = cep;
        this.numero = numero;
        this.complemento = complemento;
        this.telefone = telefone;
        this.senha = senha;
    }

    public Usuario(Long id, String nomeCompleto, Integer idade, String cpf, String cep,
                   Integer numero, String complemento, String telefone, String senha, Long versao) {
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.idade = idade;
        this.cpf = cpf;
        this.cep = cep;
        this.numero = numero;
        this.complemento = complemento;
        this.telefone = telefone;
        this.senha = senha;
        this.versao = versao;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public void incrementarVersao() {
        this.versao = Objects.requireNonNullElse(versao, 0L) + 1;
    }

    public boolean isMaiorDeIdade() {
        return this.idade != null && this.idade >= 18;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nomeCompleto='" + nomeCompleto + "', cpf='" + cpf + "', versao=" + versao + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id) && Objects.equals(cpf, usuario.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cpf);
    }
}