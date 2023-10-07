package br.com.pessoa.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * Um DTO para a entidade {@link br.com.pessoa.domain.Endereco}.
 */
public class EnderecoDTO implements Serializable {

    private Long id;

    private String logradouro;

    private String cep;

    private String numero;

    private String cidade;

    private Boolean enderecoPrincipal;

    private PessoaDTO pessoa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Boolean getEnderecoPrincipal() {
        return enderecoPrincipal;
    }

    public void setEnderecoPrincipal(Boolean enderecoPrincipal) {
        this.enderecoPrincipal = enderecoPrincipal;
    }

    public PessoaDTO getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaDTO pessoa) {
        this.pessoa = pessoa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EnderecoDTO)) {
            return false;
        }

        EnderecoDTO enderecoDTO = (EnderecoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, enderecoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "EnderecoDTO{" +
            "id=" + getId() +
            ", logradouro='" + getLogradouro() + "'" +
            ", cep='" + getCep() + "'" +
            ", numero='" + getNumero() + "'" +
            ", cidade='" + getCidade() + "'" +
            ", enderecoPrincipal='" + getEnderecoPrincipal() + "'" +
            ", pessoa=" + getPessoa() +
            "}";
    }
}
