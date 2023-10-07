package br.com.pessoa.service.criteria;

import java.io.Serializable;
import java.util.Objects;

import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Classe de critérios para a entidade {@link br.com.pessoa.domain.Pessoa}. Esta classe é usada
 * em {@link br.com.pessoa.web.rest.PessoaResource} para receber todas as opções de filtragem possíveis de
 * os parâmetros de solicitação Http GET.
 * Por exemplo, o seguinte poderia ser uma solicitação válida:
 * {@code /pessoas?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * Como o Spring não consegue converter os tipos corretamente, a menos que uma classe {@link Filter} específica seja usada, precisamos usar
 * corrigir filtros específicos do tipo.
 */

public class PessoaCriteria implements Serializable, Criteria {

	private static final long serialVersionUID = 1L;

	private LongFilter id;

	private StringFilter nome;

	private LocalDateFilter dataNascimento;

	private LongFilter enderecoId;

	private Boolean distinct;

	public PessoaCriteria() {}

	public PessoaCriteria(PessoaCriteria other) {
		this.id = other.id == null ? null : other.id.copy();
		this.nome = other.nome == null ? null : other.nome.copy();
		this.dataNascimento = other.dataNascimento == null ? null : other.dataNascimento.copy();
		this.enderecoId = other.enderecoId == null ? null : other.enderecoId.copy();
		this.distinct = other.distinct;
	}

	@Override
	public PessoaCriteria copy() {
		return new PessoaCriteria(this);
	}

	public LongFilter getId() {
		return id;
	}

	public LongFilter id() {
		if (id == null) {
			id = new LongFilter();
		}
		return id;
	}

	public void setId(LongFilter id) {
		this.id = id;
	}

	public StringFilter getNome() {
		return nome;
	}

	public StringFilter nome() {
		if (nome == null) {
			nome = new StringFilter();
		}
		return nome;
	}

	public void setNome(StringFilter nome) {
		this.nome = nome;
	}

	public LocalDateFilter getDataNascimento() {
		return dataNascimento;
	}

	public LocalDateFilter dataNascimento() {
		if (dataNascimento == null) {
			dataNascimento = new LocalDateFilter();
		}
		return dataNascimento;
	}

	public void setDataNascimento(LocalDateFilter dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public LongFilter getEnderecoId() {
		return enderecoId;
	}

	public LongFilter enderecoId() {
		if (enderecoId == null) {
			enderecoId = new LongFilter();
		}
		return enderecoId;
	}

	public void setEnderecoId(LongFilter enderecoId) {
		this.enderecoId = enderecoId;
	}

	public Boolean getDistinct() {
		return distinct;
	}

	public void setDistinct(Boolean distinct) {
		this.distinct = distinct;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final PessoaCriteria that = (PessoaCriteria) o;
		return (
				Objects.equals(id, that.id) &&
				Objects.equals(nome, that.nome) &&
				Objects.equals(dataNascimento, that.dataNascimento) &&
				Objects.equals(enderecoId, that.enderecoId) &&
				Objects.equals(distinct, that.distinct)
				);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nome, dataNascimento, enderecoId, distinct);
	}

	@Override
	public String toString() {
		return "PessoaCriteria{" +
				(id != null ? "id=" + id + ", " : "") +
				(nome != null ? "nome=" + nome + ", " : "") +
				(dataNascimento != null ? "dataNascimento=" + dataNascimento + ", " : "") +
				(enderecoId != null ? "enderecoId=" + enderecoId + ", " : "") +
				(distinct != null ? "distinct=" + distinct + ", " : "") +
				"}";
	}
}
