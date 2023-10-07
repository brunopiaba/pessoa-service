package br.com.pessoa.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.pessoa.domain.Endereco;
import br.com.pessoa.domain.Pessoa;
import br.com.pessoa.repository.PessoaRepository;
import br.com.pessoa.service.criteria.PessoaCriteria;
import br.com.pessoa.service.dto.PessoaDTO;
import br.com.pessoa.service.mapper.PessoaMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Testes de integração para o controlador REST {@link PessoaResource}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PessoaResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATA_NASCIMENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_NASCIMENTO = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATA_NASCIMENTO = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/pessoas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaMapper pessoaMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPessoaMockMvc;

    private Pessoa pessoa;

    /**
     * Cria uma entidade para este teste.
     *
     * Este é um método estático, pois testes para outras entidades também podem precisar dele,
     * se eles testarem uma entidade que requer a entidade atual.
     */
    public static Pessoa createEntity(EntityManager em) {
        Pessoa pessoa = new Pessoa().nome(DEFAULT_NOME).dataNascimento(DEFAULT_DATA_NASCIMENTO);
        return pessoa;
    }

    /**
     * Cria uma entidade atualizada para este teste.
     *
     * Este é um método estático, pois testes para outras entidades também podem precisar dele,
     * se eles testarem uma entidade que requer a entidade atual.
     */
    public static Pessoa createUpdatedEntity(EntityManager em) {
        Pessoa pessoa = new Pessoa().nome(UPDATED_NOME).dataNascimento(UPDATED_DATA_NASCIMENTO);
        return pessoa;
    }

    @BeforeEach
    public void initTest() {
        pessoa = createEntity(em);
    }

    @Test
    @Transactional
    void createPessoa() throws Exception {
        int databaseSizeBeforeCreate = pessoaRepository.findAll().size();
        // Cria a Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);
        restPessoaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isCreated());

        // Valida a Pessoa no banco de dados
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeCreate + 1);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testPessoa.getDataNascimento()).isEqualTo(DEFAULT_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void createPessoaWithExistingId() throws Exception {
        // Cria a Pessoa com um ID existente
        pessoa.setId(1L);
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        int databaseSizeBeforeCreate = pessoaRepository.findAll().size();

        // Uma entidade com um ID existente não pode ser criada, então esta chamada de API deve falhar
        restPessoaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO)))
            .andExpect(status().isBadRequest());

        // Valida a Pessoa no banco de dados
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPessoas() throws Exception {
        // Inicializa o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obter todas as pessoaList
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pessoa.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].dataNascimento").value(hasItem(DEFAULT_DATA_NASCIMENTO.toString())));
    }


    @Test
    @Transactional
    void getPessoa() throws Exception {
        // Inicialize o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtenha a pessoa
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL_ID, pessoa.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pessoa.getId().intValue()))
            .andExpect(jsonPath("$.nome").value(DEFAULT_NOME))
            .andExpect(jsonPath("$.dataNascimento").value(DEFAULT_DATA_NASCIMENTO.toString()));
    }

    @Test
    @Transactional
    void getPessoasByIdFiltering() throws Exception {
        // Inicialize o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        Long id = pessoa.getId();

        defaultPessoaShouldBeFound("id.equals=" + id);
        defaultPessoaShouldNotBeFound("id.notEquals=" + id);

        defaultPessoaShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPessoaShouldNotBeFound("id.greaterThan=" + id);

        defaultPessoaShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPessoaShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPessoasByNomeIsEqualToSomething() throws Exception {
        // Inicialize o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtenha todas as pessoaList onde nome é igual a DEFAULT_NOME
        defaultPessoaShouldBeFound("nome.equals=" + DEFAULT_NOME);

        // Obtenha todas as pessoaList onde nome é igual a UPDATED_NOME
        defaultPessoaShouldNotBeFound("nome.equals=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllPessoasByNomeIsInShouldWork() throws Exception {
        // Inicialize o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtenha todas as pessoaList onde nome está em DEFAULT_NOME ou UPDATED_NOME
        defaultPessoaShouldBeFound("nome.in=" + DEFAULT_NOME + "," + UPDATED_NOME);

        // Obtenha todas as pessoaList onde nome é igual a UPDATED_NOME
        defaultPessoaShouldNotBeFound("nome.in=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllPessoasByNomeIsNullOrNotNull() throws Exception {
        // Inicialize o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtenha todas as pessoaList onde nome não é nulo
        defaultPessoaShouldBeFound("nome.specified=true");

        // Obtenha todas as pessoaList onde nome é nulo
        defaultPessoaShouldNotBeFound("nome.specified=false");
    }

    @Test
    @Transactional
    void getAllPessoasByNomeContainsSomething() throws Exception {
        // Inicialize o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtenha todas as pessoaList onde nome contém DEFAULT_NOME
        defaultPessoaShouldBeFound("nome.contains=" + DEFAULT_NOME);

        // Obtenha todas as pessoaList onde nome contém UPDATED_NOME
        defaultPessoaShouldNotBeFound("nome.contains=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllPessoasByNomeNotContainsSomething() throws Exception {
        // Inicialize o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtenha todas as pessoaList onde nome não contém DEFAULT_NOME
        defaultPessoaShouldNotBeFound("nome.doesNotContain=" + DEFAULT_NOME);

        // Obtenha todas as pessoaList onde nome não contém UPDATED_NOME
        defaultPessoaShouldBeFound("nome.doesNotContain=" + UPDATED_NOME);
    }

    @Test
    @Transactional
    void getAllPessoasByDataNascimentoIsEqualToSomething() throws Exception {
        // Inicialize o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtenha todas as pessoaList onde dataNascimento é igual a DEFAULT_DATA_NASCIMENTO
        defaultPessoaShouldBeFound("dataNascimento.equals=" + DEFAULT_DATA_NASCIMENTO);

        // Obtenha todas as pessoaList onde dataNascimento é igual a UPDATED_DATA_NASCIMENTO
        defaultPessoaShouldNotBeFound("dataNascimento.equals=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByDataNascimentoIsInShouldWork() throws Exception {
        // Inicialize o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtenha todas as pessoaList onde dataNascimento está em DEFAULT_DATA_NASCIMENTO ou UPDATED_DATA_NASCIMENTO
        defaultPessoaShouldBeFound("dataNascimento.in=" + DEFAULT_DATA_NASCIMENTO + "," + UPDATED_DATA_NASCIMENTO);

        // Obtenha todas as pessoaList onde dataNascimento é igual a UPDATED_DATA_NASCIMENTO
        defaultPessoaShouldNotBeFound("dataNascimento.in=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByDataNascimentoIsNullOrNotNull() throws Exception {
        // Inicialize o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtenha todas as pessoaList onde dataNascimento não é nulo
        defaultPessoaShouldBeFound("dataNascimento.specified=true");

        // Obtenha todas as pessoaList onde dataNascimento é nulo
        defaultPessoaShouldNotBeFound("dataNascimento.specified=false");
    }


    @Test
    @Transactional
    void getAllPessoasByDataNascimentoIsGreaterThanOrEqualToSomething() throws Exception {
        // Inicializa o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtém todas as pessoaList onde dataNascimento é maior ou igual a DEFAULT_DATA_NASCIMENTO
        defaultPessoaShouldBeFound("dataNascimento.greaterThanOrEqual=" + DEFAULT_DATA_NASCIMENTO);

        // Obtém todas as pessoaList onde dataNascimento é maior ou igual a UPDATED_DATA_NASCIMENTO
        defaultPessoaShouldNotBeFound("dataNascimento.greaterThanOrEqual=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByDataNascimentoIsLessThanOrEqualToSomething() throws Exception {
        // Inicializa o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtém todas as pessoaList onde dataNascimento é menor ou igual a DEFAULT_DATA_NASCIMENTO
        defaultPessoaShouldBeFound("dataNascimento.lessThanOrEqual=" + DEFAULT_DATA_NASCIMENTO);

        // Obtém todas as pessoaList onde dataNascimento é menor ou igual a SMALLER_DATA_NASCIMENTO
        defaultPessoaShouldNotBeFound("dataNascimento.lessThanOrEqual=" + SMALLER_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByDataNascimentoIsLessThanSomething() throws Exception {
        // Inicializa o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtém todas as pessoaList onde dataNascimento é menor que DEFAULT_DATA_NASCIMENTO
        defaultPessoaShouldNotBeFound("dataNascimento.lessThan=" + DEFAULT_DATA_NASCIMENTO);

        // Obtém todas as pessoaList onde dataNascimento é menor que UPDATED_DATA_NASCIMENTO
        defaultPessoaShouldBeFound("dataNascimento.lessThan=" + UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByDataNascimentoIsGreaterThanSomething() throws Exception {
        // Inicializa o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        // Obtém todas as pessoaList onde dataNascimento é maior que DEFAULT_DATA_NASCIMENTO
        defaultPessoaShouldNotBeFound("dataNascimento.greaterThan=" + DEFAULT_DATA_NASCIMENTO);

        // Obtém todas as pessoaList onde dataNascimento é maior que SMALLER_DATA_NASCIMENTO
        defaultPessoaShouldBeFound("dataNascimento.greaterThan=" + SMALLER_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void getAllPessoasByEnderecoIsEqualToSomething() throws Exception {
        Endereco endereco;
        if (TestUtil.findAll(em, Endereco.class).isEmpty()) {
            pessoaRepository.saveAndFlush(pessoa);
            endereco = EnderecoResourceIT.createEntity(em);
        } else {
            endereco = TestUtil.findAll(em, Endereco.class).get(0);
        }
        em.persist(endereco);
        em.flush();
        pessoa.addEndereco(endereco);
        pessoaRepository.saveAndFlush(pessoa);
        Long enderecoId = endereco.getId();

        // Obtém todas as pessoaList onde endereco é igual a enderecoId
        defaultPessoaShouldBeFound("enderecoId.equals=" + enderecoId);

        // Obtém todas as pessoaList onde endereco é igual a (enderecoId + 1)
        defaultPessoaShouldNotBeFound("enderecoId.equals=" + (enderecoId + 1));
    }

    /**
     * Executa a pesquisa e verifica se a entidade padrão é retornada.
     */
    private void defaultPessoaShouldBeFound(String filter) throws Exception {
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pessoa.getId().intValue())))
            .andExpect(jsonPath("$.[*].nome").value(hasItem(DEFAULT_NOME)))
            .andExpect(jsonPath("$.[*].dataNascimento").value(hasItem(DEFAULT_DATA_NASCIMENTO.toString())));

        // Verifica se a chamada de contagem também retorna 1
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executa a pesquisa e verifica se a entidade padrão não é retornada.
     */
    private void defaultPessoaShouldNotBeFound(String filter) throws Exception {
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Verifica se a chamada de contagem também retorna 0
        restPessoaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPessoa() throws Exception {
        // Obtém a pessoa
        restPessoaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPessoa() throws Exception {
        // Inicializa o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();

        // Atualiza a pessoa
        Pessoa updatedPessoa = pessoaRepository.findById(pessoa.getId()).get();
        // Desconecta da sessão para que as atualizações em updatedPessoa não sejam salvas diretamente no banco de dados
        em.detach(updatedPessoa);
        updatedPessoa.nome(UPDATED_NOME).dataNascimento(UPDATED_DATA_NASCIMENTO);
        PessoaDTO pessoaDTO = pessoaMapper.toDto(updatedPessoa);

        restPessoaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pessoaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isOk());

        // Valida a Pessoa no banco de dados
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPessoa.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void putNonExistingPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Cria a Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // Se a entidade não tiver um ID, ela lançará BadRequestAlertException
        restPessoaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pessoaDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isBadRequest());

        // Valida a Pessoa no banco de dados
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Cria a Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // Se o ID da URL não corresponder ao ID da entidade, ele lançará BadRequestAlertException
        restPessoaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isBadRequest());

        // Valida a Pessoa no banco de dados
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Cria a Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // Se o ID da URL não corresponder ao ID da entidade, ele lançará BadRequestAlertException
        restPessoaMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Valida a Pessoa no banco de dados
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePessoaWithPatch() throws Exception {
        // Inicializa o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();

        // Atualiza a pessoa usando atualização parcial
        Pessoa partialUpdatedPessoa = new Pessoa();
        partialUpdatedPessoa.setId(pessoa.getId());

        partialUpdatedPessoa.nome(UPDATED_NOME).dataNascimento(UPDATED_DATA_NASCIMENTO);

        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPessoa.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoa))
            )
            .andExpect(status().isOk());

        // Valida a Pessoa no banco de dados
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPessoa.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void fullUpdatePessoaWithPatch() throws Exception {
        // Inicializa o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();

        // Atualiza a pessoa usando atualização parcial
        Pessoa partialUpdatedPessoa = new Pessoa();
        partialUpdatedPessoa.setId(pessoa.getId());

        partialUpdatedPessoa.nome(UPDATED_NOME).dataNascimento(UPDATED_DATA_NASCIMENTO);

        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPessoa.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoa))
            )
            .andExpect(status().isOk());

        // Valida a Pessoa no banco de dados
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPessoa.getDataNascimento()).isEqualTo(UPDATED_DATA_NASCIMENTO);
    }

    @Test
    @Transactional
    void patchNonExistingPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Cria a Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // Se a entidade não tiver um ID, ela lançará BadRequestAlertException
        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pessoaDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isBadRequest());

        // Valida a Pessoa no banco de dados
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Cria a Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // Se o ID da URL não corresponder ao ID da entidade, ele lançará BadRequestAlertException
        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isBadRequest());

        // Valida a Pessoa no banco de dados
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().size();
        pessoa.setId(count.incrementAndGet());

        // Cria a Pessoa
        PessoaDTO pessoaDTO = pessoaMapper.toDto(pessoa);

        // Se o ID da URL não corresponder ao ID da entidade, ele lançará BadRequestAlertException
        restPessoaMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(pessoaDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Valida a Pessoa no banco de dados
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePessoa() throws Exception {
        // Inicializa o banco de dados
        pessoaRepository.saveAndFlush(pessoa);

        int databaseSizeBeforeDelete = pessoaRepository.findAll().size();

        // Exclui a pessoa
        restPessoaMockMvc
            .perform(delete(ENTITY_API_URL_ID, pessoa.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Valida se o banco de dados contém um item a menos
        List<Pessoa> pessoaList = pessoaRepository.findAll();
        assertThat(pessoaList).hasSize(databaseSizeBeforeDelete - 1);
    }

}
