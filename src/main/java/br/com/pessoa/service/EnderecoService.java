package br.com.pessoa.service;

import br.com.pessoa.service.dto.EnderecoDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface de serviço para gerenciamento de {@link br.com.pessoa.domain.Endereco}.
 */
public interface EnderecoService {
	/**
     * Salve um endereco.
     *
     * @param enderecoDTO a entidade a ser salva.
     * @return a entidade persistida.
     */
    EnderecoDTO save(EnderecoDTO enderecoDTO);

    /**
     * Atualiza um endereco.
     *
     * @param enderecoDTO a entidade a ser atualizada.
     * @return a entidade persistida.
     */
    EnderecoDTO update(EnderecoDTO enderecoDTO);

    /**
     * Atualiza parcialmente um endereco.
     *
     * @param enderecoDTO a entidade a ser atualizada parcialmente.
     * @return a entidade persistida.
     */
    Optional<EnderecoDTO> partialUpdate(EnderecoDTO enderecoDTO);

    /**
     * Pegue todos os enderecos.
     *
     * @param pageable as informações de paginação.
     * @return a lista de entidades.
     */
    Page<EnderecoDTO> findAll(Pageable pageable);

    /**
     * Obtenha o endereco "id".
     *
     * @param id o id da entidade.
     * @return a entidade.
     */
    Optional<EnderecoDTO> findOne(Long id);

    /**
     * Exclua o "id" endereco.
     *
     * @param id o id da entidade.
     */
    void delete(Long id);
}
