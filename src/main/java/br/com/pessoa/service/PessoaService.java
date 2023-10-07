package br.com.pessoa.service;

import br.com.pessoa.service.dto.PessoaDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link br.com.pessoa.domain.Pessoa}.
 */
public interface PessoaService {
	/**
     * Salve uma pessoa.
     *
     * @param pessoaDTO a entidade a ser salva.
     * @return a entidade persistida.
     */
    PessoaDTO save(PessoaDTO pessoaDTO);

    /**
     *Atualiza uma pessoa.
     *
     * @param pessoaDTO a entidade a ser atualizada.
     * @return a entidade persistida.
     */
    PessoaDTO update(PessoaDTO pessoaDTO);

    /**
     *Atualiza parcialmente uma pessoa.
     *
     * @param pessoaDTO a entidade a ser atualizada parcialmente.
     * @return a entidade persistida.
     */
    Optional<PessoaDTO> partialUpdate(PessoaDTO pessoaDTO);

    /**
     * Pegue todas as pessoas.
     *
     * @param pageable as informações de paginação.
     * @return a lista de entidades.
     */
    Page<PessoaDTO> findAll(Pageable pageable);

    /**
     * Obtenha o "id" da pessoa.
     *
     * @param id o id da entidade.
     * @return a entidade.
     */
    Optional<PessoaDTO> findOne(Long id);

    /**
     * Exclua o "id" pessoa.
     *
     * @param id o id da entidade.
     */
    void delete(Long id);
}
