package br.com.pessoa.web.rest;

import br.com.pessoa.repository.EnderecoRepository;
import br.com.pessoa.service.EnderecoService;
import br.com.pessoa.service.dto.EnderecoDTO;
import br.com.pessoa.web.rest.errors.BadRequestAlertException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller para gerenciamento de {@link br.com.pessoa.domain.Endereco}.
 */
@RestController
@RequestMapping("/api")
public class EnderecoResource {

    private final Logger log = LoggerFactory.getLogger(EnderecoResource.class);

    private static final String ENTITY_NAME = "pessoaEndereco";

    @Value("${spring.application.name}")
    private String applicationName;

    private final EnderecoService enderecoService;

    private final EnderecoRepository enderecoRepository;

    public EnderecoResource(EnderecoService enderecoService, EnderecoRepository enderecoRepository) {
        this.enderecoService = enderecoService;
        this.enderecoRepository = enderecoRepository;
    }

    /**
     * {@code POST /enderecos} : Crie um novo endereco.
     *
     * @param enderecoDTO o enderecoDTO a ser criado.
     * @return o {@link ResponseEntity} com status {@code 201 (Created)} e com corpo o novo enderecoDTO, ou com status {@code 400 (Bad Request)} caso o endereco já possua um ID.
     * @throws URISyntaxException se a sintaxe do URI do local estiver incorreta.
     */
    @PostMapping("/enderecos")
    public ResponseEntity<EnderecoDTO> createEndereco(@RequestBody EnderecoDTO enderecoDTO) throws URISyntaxException {
        log.debug("REST request to save Endereco : {}", enderecoDTO);
        if (enderecoDTO.getId() != null) {
            throw new BadRequestAlertException("A new endereco cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EnderecoDTO result = enderecoService.save(enderecoDTO);
        return ResponseEntity
            .created(new URI("/api/enderecos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT /enderecos/:id} : Atualiza um endereco existente.
     *
     * @param id o id do enderecoDTO a ser salvo.
     * @param enderecoDTO o enderecoDTO a ser atualizado.
     * @return o {@link ResponseEntity} com status {@code 200 (OK)} e com corpo o enderecoDTO atualizado,
     * ou com status {@code 400 (Bad Request)} se o enderecoDTO não for válido,
     * ou com status {@code 500 (Internal Server Error)} se o enderecoDTO não puder ser atualizado.
     * @throws URISyntaxException se a sintaxe do URI do local estiver incorreta.
     */
    @PutMapping("/enderecos/{id}")
    public ResponseEntity<EnderecoDTO> updateEndereco(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EnderecoDTO enderecoDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Endereco : {}, {}", id, enderecoDTO);
        if (enderecoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, enderecoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!enderecoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        EnderecoDTO result = enderecoService.update(enderecoDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, enderecoDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH /enderecos/:id} : Atualizações parciais dados campos de um endereco existente, o campo será ignorado se for nulo
     *
     * @param id o id do enderecoDTO a ser salvo.
     * @param enderecoDTO o enderecoDTO a ser atualizado.
     * @return o {@link ResponseEntity} com status {@code 200 (OK)} e com corpo o enderecoDTO atualizado,
     * ou com status {@code 400 (Bad Request)} se o enderecoDTO não for válido,
     * ou com status {@code 404 (Not Found)} se o enderecoDTO não for encontrado,
     * ou com status {@code 500 (Internal Server Error)} se o enderecoDTO não puder ser atualizado.
     * @throws URISyntaxException se a sintaxe do URI do local estiver incorreta.
     */
    @PatchMapping(value = "/enderecos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EnderecoDTO> partialUpdateEndereco(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EnderecoDTO enderecoDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Endereco partially : {}, {}", id, enderecoDTO);
        if (enderecoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, enderecoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!enderecoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EnderecoDTO> result = enderecoService.partialUpdate(enderecoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, enderecoDTO.getId().toString())
        );
    }

    /**
     * {@code GET /enderecos} : obtém todos os enderecos.
     *
     * @param pageable as informações de paginação.
     * @return o {@link ResponseEntity} com status {@code 200 (OK)} e a lista de enderecos no corpo.
     */
    @GetMapping("/enderecos")
    public ResponseEntity<List<EnderecoDTO>> getAllEnderecos(Pageable pageable) {
        log.debug("REST request to get a page of Enderecos");
        Page<EnderecoDTO> page = enderecoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET /enderecos/:id} : obtém o "id" endereco.
     *
     * @param id o id do enderecoDTO a ser recuperado.
     * @return o {@link ResponseEntity} com status {@code 200 (OK)} e com corpo enderecoDTO, ou com status {@code 404 (Not Found)}.
     */
    @GetMapping("/enderecos/{id}")
    public ResponseEntity<EnderecoDTO> getEndereco(@PathVariable Long id) {
        log.debug("REST request to get Endereco : {}", id);
        Optional<EnderecoDTO> enderecoDTO = enderecoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(enderecoDTO);
    }

    /**
     * {@code DELETE /enderecos/:id} : exclui o "id" endereco.
     *
     * @param id o id do enderecoDTO a ser excluído.
     * @return o {@link ResponseEntity} com status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/enderecos/{id}")
    public ResponseEntity<Void> deleteEndereco(@PathVariable Long id) {
        log.debug("REST request to delete Endereco : {}", id);
        enderecoService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
