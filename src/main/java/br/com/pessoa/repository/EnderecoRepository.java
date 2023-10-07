package br.com.pessoa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.pessoa.domain.Endereco;

/**
 * Repositório Spring Data JPA para a entidade Endereco.
 */
@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {}
