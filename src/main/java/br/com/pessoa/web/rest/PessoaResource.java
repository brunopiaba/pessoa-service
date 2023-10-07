package br.com.pessoa.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.pessoa.repository.PessoaRepository;
import br.com.pessoa.service.PessoaQueryService;
import br.com.pessoa.service.PessoaService;
import br.com.pessoa.service.criteria.PessoaCriteria;
import br.com.pessoa.service.dto.PessoaDTO;
import br.com.pessoa.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link br.com.pessoa.domain.Pessoa}.
 */
@RestController
@RequestMapping("/api")
public class PessoaResource {

    private final Logger log = LoggerFactory.getLogger(PessoaResource.class);

    private static final String ENTITY_NAME = "pessoaPessoa";

    @Value("${spring.application.name}")
    private String applicationName;

    private final PessoaService pessoaService;

    private final PessoaRepository pessoaRepository;

    private final PessoaQueryService pessoaQueryService;

    public PessoaResource(PessoaService pessoaService, PessoaRepository pessoaRepository, PessoaQueryService pessoaQueryService) {
        this.pessoaService = pessoaService;
        this.pessoaRepository = pessoaRepository;
        this.pessoaQueryService = pessoaQueryService;
    }

    /**
     * {@code POST /pessoas} : Crie uma nova pessoa.
     *
     * @param pessoaDTO a pessoaDTO a ser criada.
     * @return o {@link ResponseEntity} com status {@code 201 (Created)} e com corpo a nova pessoaDTO, ou com status {@code 400 (Bad Request)} caso a pessoa já possua um ID.
     * @throws URISyntaxException se a sintaxe do URI do local estiver incorreta.
     */
    @PostMapping("/pessoas")
    public ResponseEntity<PessoaDTO> createPessoa(@RequestBody PessoaDTO pessoaDTO) throws URISyntaxException {
        log.debug("REST request to save Pessoa : {}", pessoaDTO);
        if (pessoaDTO.getId() != null) {
            throw new BadRequestAlertException("A new pessoa cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PessoaDTO result = pessoaService.save(pessoaDTO);
        return ResponseEntity
            .created(new URI("/api/pessoas/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT /pessoas/:id} : Atualiza uma pessoa existente.
     *
     * @param id o id da pessoaDTO a ser salva.
     * @param pessoaDTO a pessoaDTO a ser atualizada.
     * @return o {@link ResponseEntity} com status {@code 200 (OK)} e com corpo a pessoaDTO atualizada,
     * ou com status {@code 400 (Bad Request)} caso a pessoaDTO não seja válida,
     * ou com status {@code 500 (Internal Server Error)} se a pessoaDTO não puder ser atualizada.
     * @throws URISyntaxException se a sintaxe do URI do local estiver incorreta.
     */
    @PutMapping("/pessoas/{id}")
    public ResponseEntity<PessoaDTO> updatePessoa(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PessoaDTO pessoaDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Pessoa : {}, {}", id, pessoaDTO);
        if (pessoaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pessoaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PessoaDTO result = pessoaService.update(pessoaDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pessoaDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH /pessoas/:id} : Atualizações parciais dados campos de uma pessoa existente, campo será ignorado se for nulo
     *
     * @param id o id da pessoaDTO a ser salva.
     * @param pessoaDTO a pessoaDTO a ser atualizada.
     * @return o {@link ResponseEntity} com status {@code 200 (OK)} e com corpo a pessoaDTO atualizada,
     * ou com status {@code 400 (Bad Request)} caso a pessoaDTO não seja válida,
     * ou com status {@code 404 (Not Found)} se a pessoaDTO não for encontrada,
     * ou com status {@code 500 (Internal Server Error)} se a pessoaDTO não puder ser atualizada.
     * @throws URISyntaxException se a sintaxe do URI do local estiver incorreta.
     */
    @PatchMapping(value = "/pessoas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PessoaDTO> partialUpdatePessoa(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PessoaDTO pessoaDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pessoa partially : {}, {}", id, pessoaDTO);
        if (pessoaDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pessoaDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pessoaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PessoaDTO> result = pessoaService.partialUpdate(pessoaDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pessoaDTO.getId().toString())
        );
    }

    /**
     * {@code GET /pessoas} : pega todas as pessoas.
     *
     * @param pageable as informações de paginação.
     * @param critérios os critérios aos quais as entidades solicitadas devem atender.
     * @return o {@link ResponseEntity} com status {@code 200 (OK)} e a lista de pessoas no corpo.
     */
    @GetMapping("/pessoas")
    public ResponseEntity<List<PessoaDTO>> getAllPessoas(
        PessoaCriteria criteria,
        Pageable pageable
    ) {
        log.debug("REST request to get Pessoas by criteria: {}", criteria);
        Page<PessoaDTO> page = pessoaQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET /pessoas/count} : conta todas as pessoas.
     *
     * @param critérios os critérios aos quais as entidades solicitadas devem atender.
     * @return o {@link ResponseEntity} com status {@code 200 (OK)} e a contagem no corpo.
     */
    @GetMapping("/pessoas/count")
    public ResponseEntity<Long> countPessoas(PessoaCriteria criteria) {
        log.debug("REST request to count Pessoas by criteria: {}", criteria);
        return ResponseEntity.ok().body(pessoaQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET /pessoas/:id} : obtém o "id" pessoa.
     *
     * @param id o id da pessoaDTO a ser recuperada.
     * @return o {@link ResponseEntity} com status {@code 200 (OK)} e com corpo pessoaDTO, ou com status {@code 404 (Not Found)}.
     */
    @GetMapping("/pessoas/{id}")
    public ResponseEntity<PessoaDTO> getPessoa(@PathVariable Long id) {
        log.debug("REST request to get Pessoa : {}", id);
        Optional<PessoaDTO> pessoaDTO = pessoaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pessoaDTO);
    }

    /**
     * {@code DELETE /pessoas/:id} : exclui o "id" pessoa.
     *
     * @param id o id da pessoaDTO a ser deletada.
     * @return o {@link ResponseEntity} com status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pessoas/{id}")
    public ResponseEntity<Void> deletePessoa(@PathVariable Long id) {
        log.debug("REST request to delete Pessoa : {}", id);
        pessoaService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
