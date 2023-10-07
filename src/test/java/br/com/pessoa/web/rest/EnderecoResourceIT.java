package br.com.pessoa.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.pessoa.domain.Endereco;
import br.com.pessoa.repository.EnderecoRepository;
import br.com.pessoa.service.dto.EnderecoDTO;
import br.com.pessoa.service.mapper.EnderecoMapper;
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
 * Testes de integração para o controlador REST {@link EnderecoResource}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class EnderecoResourceIT {

    private static final String DEFAULT_LOGRADOURO = "AAAAAAAAAA";
    private static final String UPDATED_LOGRADOURO = "BBBBBBBBBB";

    private static final String DEFAULT_CEP = "AAAAAAAAAA";
    private static final String UPDATED_CEP = "BBBBBBBBBB";

    private static final String DEFAULT_NUMERO = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO = "BBBBBBBBBB";

    private static final String DEFAULT_CIDADE = "AAAAAAAAAA";
    private static final String UPDATED_CIDADE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENDERECO_PRINCIPAL = false;
    private static final Boolean UPDATED_ENDERECO_PRINCIPAL = true;

    private static final String ENTITY_API_URL = "/api/enderecos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private EnderecoMapper enderecoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnderecoMockMvc;

    private Endereco endereco;

    /**
     * Cria uma entidade para este teste.
     *
     * Este é um método estático, pois testes para outras entidades também podem precisar dele,
     * se eles testarem uma entidade que requer a entidade atual.
     */
    public static Endereco createEntity(EntityManager em) {
        Endereco endereco = new Endereco()
            .logradouro(DEFAULT_LOGRADOURO)
            .cep(DEFAULT_CEP)
            .numero(DEFAULT_NUMERO)
            .cidade(DEFAULT_CIDADE)
            .enderecoPrincipal(DEFAULT_ENDERECO_PRINCIPAL);
        return endereco;
    }

    /**
     * Cria uma entidade atualizada para este teste.
     *
     * Este é um método estático, pois testes para outras entidades também podem precisar dele,
     * se eles testarem uma entidade que requer a entidade atual.
     */
    public static Endereco createUpdatedEntity(EntityManager em) {
        Endereco endereco = new Endereco()
            .logradouro(UPDATED_LOGRADOURO)
            .cep(UPDATED_CEP)
            .numero(UPDATED_NUMERO)
            .cidade(UPDATED_CIDADE)
            .enderecoPrincipal(UPDATED_ENDERECO_PRINCIPAL);
        return endereco;
    }

    @BeforeEach
    public void initTest() {
        endereco = createEntity(em);
    }

    @Test
    @Transactional
    void createEndereco() throws Exception {
        int databaseSizeBeforeCreate = enderecoRepository.findAll().size();
        // Cria o Endereco
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);
        restEnderecoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enderecoDTO)))
            .andExpect(status().isCreated());

        // Valida o Endereco no banco de dados
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeCreate + 1);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getLogradouro()).isEqualTo(DEFAULT_LOGRADOURO);
        assertThat(testEndereco.getCep()).isEqualTo(DEFAULT_CEP);
        assertThat(testEndereco.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testEndereco.getCidade()).isEqualTo(DEFAULT_CIDADE);
        assertThat(testEndereco.getEnderecoPrincipal()).isEqualTo(DEFAULT_ENDERECO_PRINCIPAL);
    }

    @Test
    @Transactional
    void createEnderecoWithExistingId() throws Exception {
        // Cria o Endereco com um ID existente
        endereco.setId(1L);
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        int databaseSizeBeforeCreate = enderecoRepository.findAll().size();

        // Uma entidade com um ID existente não pode ser criada, portanto, esta chamada de API deve falhar
        restEnderecoMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enderecoDTO)))
            .andExpect(status().isBadRequest());

        // Valida o Endereco no banco de dados
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEnderecos() throws Exception {
        // Inicializa o banco de dados
        enderecoRepository.saveAndFlush(endereco);

        // Obtem todos os enderecoList
        restEnderecoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(endereco.getId().intValue())))
            .andExpect(jsonPath("$.[*].logradouro").value(hasItem(DEFAULT_LOGRADOURO)))
            .andExpect(jsonPath("$.[*].cep").value(hasItem(DEFAULT_CEP)))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].cidade").value(hasItem(DEFAULT_CIDADE)))
            .andExpect(jsonPath("$.[*].enderecoPrincipal").value(hasItem(DEFAULT_ENDERECO_PRINCIPAL.booleanValue())));
    }

    @Test
    @Transactional
    void getEndereco() throws Exception {
        // Inicializa o banco de dados
        enderecoRepository.saveAndFlush(endereco);

        // Obtem o endereco
        restEnderecoMockMvc
            .perform(get(ENTITY_API_URL_ID, endereco.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(endereco.getId().intValue()))
            .andExpect(jsonPath("$.logradouro").value(DEFAULT_LOGRADOURO))
            .andExpect(jsonPath("$.cep").value(DEFAULT_CEP))
            .andExpect(jsonPath("$.numero").value(DEFAULT_NUMERO))
            .andExpect(jsonPath("$.cidade").value(DEFAULT_CIDADE))
            .andExpect(jsonPath("$.enderecoPrincipal").value(DEFAULT_ENDERECO_PRINCIPAL.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingEndereco() throws Exception {
        // Obtem o endereco
        restEnderecoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEndereco() throws Exception {
        // Inicializa o banco de dados
        enderecoRepository.saveAndFlush(endereco);

        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();

        // Atualiza o endereco
        Endereco updatedEndereco = enderecoRepository.findById(endereco.getId()).get();
        // Desconecta da sessão para que as atualizações no updatedEndereco não sejam salvas diretamente no banco de dados
        em.detach(updatedEndereco);
        updatedEndereco
            .logradouro(UPDATED_LOGRADOURO)
            .cep(UPDATED_CEP)
            .numero(UPDATED_NUMERO)
            .cidade(UPDATED_CIDADE)
            .enderecoPrincipal(UPDATED_ENDERECO_PRINCIPAL);
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(updatedEndereco);

        restEnderecoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, enderecoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enderecoDTO))
            )
            .andExpect(status().isOk());

        // Valida o Endereco no banco de dados
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getLogradouro()).isEqualTo(UPDATED_LOGRADOURO);
        assertThat(testEndereco.getCep()).isEqualTo(UPDATED_CEP);
        assertThat(testEndereco.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testEndereco.getCidade()).isEqualTo(UPDATED_CIDADE);
        assertThat(testEndereco.getEnderecoPrincipal()).isEqualTo(UPDATED_ENDERECO_PRINCIPAL);
    }


    @Test
    @Transactional
    void putNonExistingEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        endereco.setId(count.incrementAndGet());

        // Cria o Endereço
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        // Se a entidade não tiver um ID, lançará uma BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, enderecoDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enderecoDTO))
            )
            .andExpect(status().isBadRequest());

        // Valida o Endereço no banco de dados
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        endereco.setId(count.incrementAndGet());

        // Cria o Endereço
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        // Se o ID da URL não coincidir com o ID da entidade, lançará uma BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(enderecoDTO))
            )
            .andExpect(status().isBadRequest());

        // Valida o Endereço no banco de dados
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        endereco.setId(count.incrementAndGet());

        // Cria o Endereço
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        // Se o ID da URL não coincidir com o ID da entidade, lançará uma BadRequestAlertException
        restEnderecoMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(enderecoDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Valida o Endereço no banco de dados
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEnderecoWithPatch() throws Exception {
        // Inicializa o banco de dados
        enderecoRepository.saveAndFlush(endereco);

        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();

        // Atualiza o Endereço usando atualização parcial
        Endereco partialUpdatedEndereco = new Endereco();
        partialUpdatedEndereco.setId(endereco.getId());

        partialUpdatedEndereco.numero(UPDATED_NUMERO).cidade(UPDATED_CIDADE).enderecoPrincipal(UPDATED_ENDERECO_PRINCIPAL);

        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEndereco.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEndereco))
            )
            .andExpect(status().isOk());

        // Valida o Endereço no banco de dados
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getLogradouro()).isEqualTo(DEFAULT_LOGRADOURO);
        assertThat(testEndereco.getCep()).isEqualTo(DEFAULT_CEP);
        assertThat(testEndereco.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testEndereco.getCidade()).isEqualTo(UPDATED_CIDADE);
        assertThat(testEndereco.getEnderecoPrincipal()).isEqualTo(UPDATED_ENDERECO_PRINCIPAL);
    }

    @Test
    @Transactional
    void fullUpdateEnderecoWithPatch() throws Exception {
        // Inicializa o banco de dados
        enderecoRepository.saveAndFlush(endereco);

        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();

        // Atualiza o Endereço usando atualização parcial
        Endereco partialUpdatedEndereco = new Endereco();
        partialUpdatedEndereco.setId(endereco.getId());

        partialUpdatedEndereco
            .logradouro(UPDATED_LOGRADOURO)
            .cep(UPDATED_CEP)
            .numero(UPDATED_NUMERO)
            .cidade(UPDATED_CIDADE)
            .enderecoPrincipal(UPDATED_ENDERECO_PRINCIPAL);

        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEndereco.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEndereco))
            )
            .andExpect(status().isOk());

        // Valida o Endereço no banco de dados
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getLogradouro()).isEqualTo(UPDATED_LOGRADOURO);
        assertThat(testEndereco.getCep()).isEqualTo(UPDATED_CEP);
        assertThat(testEndereco.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testEndereco.getCidade()).isEqualTo(UPDATED_CIDADE);
        assertThat(testEndereco.getEnderecoPrincipal()).isEqualTo(UPDATED_ENDERECO_PRINCIPAL);
    }

    @Test
    @Transactional
    void patchNonExistingEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        endereco.setId(count.incrementAndGet());

        // Cria o Endereço
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        // Se a entidade não tiver um ID, lançará uma BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, enderecoDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(enderecoDTO))
            )
            .andExpect(status().isBadRequest());

        // Valida o Endereço no banco de dados
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        endereco.setId(count.incrementAndGet());

        // Cria o Endereço
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        // Se o ID da URL não coincidir com o ID da entidade, lançará uma BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(enderecoDTO))
            )
            .andExpect(status().isBadRequest());

        // Valida o Endereço no banco de dados
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().size();
        endereco.setId(count.incrementAndGet());

        // Cria o Endereço
        EnderecoDTO enderecoDTO = enderecoMapper.toDto(endereco);

        // Se o ID da URL não coincidir com o ID da entidade, lançará uma BadRequestAlertException
        restEnderecoMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(enderecoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Valida o Endereço no banco de dados
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEndereco() throws Exception {
        // Inicializa o banco de dados
        enderecoRepository.saveAndFlush(endereco);

        int databaseSizeBeforeDelete = enderecoRepository.findAll().size();

        // Exclui o Endereço
        restEnderecoMockMvc
            .perform(delete(ENTITY_API_URL_ID, endereco.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Valida que o banco de dados contém um item a menos
        List<Endereco> enderecoList = enderecoRepository.findAll();
        assertThat(enderecoList).hasSize(databaseSizeBeforeDelete - 1);
    }

}
