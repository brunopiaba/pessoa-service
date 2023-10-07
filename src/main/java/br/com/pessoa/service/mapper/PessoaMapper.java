package br.com.pessoa.service.mapper;

import br.com.pessoa.domain.Pessoa;
import br.com.pessoa.service.dto.PessoaDTO;
import org.mapstruct.*;

/**
 * Mapper para a entidade {@link Pessoa} e seu DTO {@link PessoaDTO}.
 */
@Mapper(componentModel = "spring")
public interface PessoaMapper extends EntityMapper<PessoaDTO, Pessoa> {}
