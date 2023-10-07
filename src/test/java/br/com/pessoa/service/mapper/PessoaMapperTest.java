package br.com.pessoa.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class PessoaMapperTest {

    private PessoaMapper pessoaMapper;

    @BeforeEach
    public void setUp() {
        pessoaMapper = new PessoaMapperImpl();
    }
}
