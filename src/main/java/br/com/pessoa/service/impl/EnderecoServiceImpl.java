package br.com.pessoa.service.impl;

import br.com.pessoa.domain.Endereco;
import br.com.pessoa.repository.EnderecoRepository;
import br.com.pessoa.service.EnderecoService;
import br.com.pessoa.service.dto.EnderecoDTO;
import br.com.pessoa.service.mapper.EnderecoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementação para gerenciamento {@link Endereco}.
 */
@Service
@Transactional
public class EnderecoServiceImpl implements EnderecoService {

    private final Logger log = LoggerFactory.getLogger(EnderecoServiceImpl.class);

    private final EnderecoRepository enderecoRepository;

    private final EnderecoMapper enderecoMapper;

    public EnderecoServiceImpl(EnderecoRepository enderecoRepository, EnderecoMapper enderecoMapper) {
        this.enderecoRepository = enderecoRepository;
        this.enderecoMapper = enderecoMapper;
    }

    @Override
    public EnderecoDTO save(EnderecoDTO enderecoDTO) {
        log.debug("Request to save Endereco : {}", enderecoDTO);
        Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
        endereco = enderecoRepository.save(endereco);
        return enderecoMapper.toDto(endereco);
    }

    @Override
    public EnderecoDTO update(EnderecoDTO enderecoDTO) {
        log.debug("Request to update Endereco : {}", enderecoDTO);
        Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
        endereco = enderecoRepository.save(endereco);
        return enderecoMapper.toDto(endereco);
    }

    @Override
    public Optional<EnderecoDTO> partialUpdate(EnderecoDTO enderecoDTO) {
        log.debug("Request to partially update Endereco : {}", enderecoDTO);

        return enderecoRepository
            .findById(enderecoDTO.getId())
            .map(existingEndereco -> {
                enderecoMapper.partialUpdate(existingEndereco, enderecoDTO);

                return existingEndereco;
            })
            .map(enderecoRepository::save)
            .map(enderecoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnderecoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Enderecos");
        return enderecoRepository.findAll(pageable).map(enderecoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EnderecoDTO> findOne(Long id) {
        log.debug("Request to get Endereco : {}", id);
        return enderecoRepository.findById(id).map(enderecoMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Endereco : {}", id);
        enderecoRepository.deleteById(id);
    }
}
