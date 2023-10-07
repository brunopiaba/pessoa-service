package br.com.pessoa.service;

import br.com.pessoa.domain.*;
import br.com.pessoa.domain.Pessoa;
import br.com.pessoa.repository.PessoaRepository;
import br.com.pessoa.service.criteria.PessoaCriteria;
import br.com.pessoa.service.dto.PessoaDTO;
import br.com.pessoa.service.mapper.PessoaMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Serviço de execução de consultas complexas para entidades {@link Pessoa} no banco de dados.
 * A entrada principal é um {@link PessoaCriteria} que é convertido em {@link Especificação},
 * de forma que todos os filtros sejam aplicados.
 * Devolve uma {@link List} de {@link PessoaDTO} ou uma {@link Page} de {@link PessoaDTO} que cumpre os critérios.
 */
@Service
@Transactional(readOnly = true)
public class PessoaQueryService extends QueryService<Pessoa> {

    private final Logger log = LoggerFactory.getLogger(PessoaQueryService.class);

    private final PessoaRepository pessoaRepository;

    private final PessoaMapper pessoaMapper;

    public PessoaQueryService(PessoaRepository pessoaRepository, PessoaMapper pessoaMapper) {
        this.pessoaRepository = pessoaRepository;
        this.pessoaMapper = pessoaMapper;
    }

    /**
     * Retorna uma {@link List} de {@link PessoaDTO} que corresponde aos critérios do banco de dados.
     * @param critérios O objeto que contém todos os filtros aos quais as entidades devem corresponder.
     * @return as entidades correspondentes.
     */
    @Transactional(readOnly = true)
    public List<PessoaDTO> findByCriteria(PessoaCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Pessoa> specification = createSpecification(criteria);
        return pessoaMapper.toDto(pessoaRepository.findAll(specification));
    }

    /**
     * Retorna uma {@link Page} de {@link PessoaDTO} que corresponde aos critérios do banco de dados.
     * @param critérios O objeto que contém todos os filtros aos quais as entidades devem corresponder.
     * @param page A página que deve ser retornada.
     * @return as entidades correspondentes.
     */
    @Transactional(readOnly = true)
    public Page<PessoaDTO> findByCriteria(PessoaCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Pessoa> specification = createSpecification(criteria);
        return pessoaRepository.findAll(specification, page).map(pessoaMapper::toDto);
    }

    /**
     * Retorna o número de entidades correspondentes no banco de dados.
     * @param critérios O objeto que contém todos os filtros aos quais as entidades devem corresponder.
     * @return o número de entidades correspondentes.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PessoaCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Pessoa> specification = createSpecification(criteria);
        return pessoaRepository.count(specification);
    }

    /**
     * Função para converter {@link PessoaCriteria} em {@link Especificação}
     * @param critérios O objeto que contém todos os filtros aos quais as entidades devem corresponder.
     * @return a {@link Especificação} correspondente da entidade.
     */
    protected Specification<Pessoa> createSpecification(PessoaCriteria criteria) {
        Specification<Pessoa> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Pessoa_.id));
            }
            if (criteria.getNome() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNome(), Pessoa_.nome));
            }
            if (criteria.getDataNascimento() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataNascimento(), Pessoa_.dataNascimento));
            }
            if (criteria.getEnderecoId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getEnderecoId(), root -> root.join(Pessoa_.enderecos, JoinType.LEFT).get(Endereco_.id))
                    );
            }
        }
        return specification;
    }
}
