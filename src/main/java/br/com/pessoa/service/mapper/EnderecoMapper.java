package br.com.pessoa.service.mapper;

import br.com.pessoa.domain.Endereco;
import br.com.pessoa.domain.Pessoa;
import br.com.pessoa.service.dto.EnderecoDTO;
import br.com.pessoa.service.dto.PessoaDTO;
import org.mapstruct.*;

/**
 * Mapper para a entidade {@link Endereco} e seu DTO {@link EnderecoDTO}.
 */
@Mapper(componentModel = "spring")
public interface EnderecoMapper extends EntityMapper<EnderecoDTO, Endereco> {
    @Mapping(target = "pessoa", source = "pessoa", qualifiedByName = "pessoaId")
    EnderecoDTO toDto(Endereco s);

    @Named("pessoaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PessoaDTO toDtoPessoaId(Pessoa pessoa);
}
