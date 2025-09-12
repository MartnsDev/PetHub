package br.com.alura.adopet.api.domain.repository;

import br.com.alura.adopet.api.domain.model.Abrigo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AbrigoRepositoryTest {

    @Autowired
    private AbrigoRepository abrigoRepository;

    private Abrigo abrigo;

    @BeforeEach
    void setUp() {
        abrigo = new Abrigo();
        abrigo.setNome("Abrigo Teste");
        abrigo.setEmail("email@test.com");
        abrigo.setTelefone("12345-6789");
        abrigoRepository.save(abrigo);
    }

    @Test
    void findByNome_RetornaAbrigo_QuandoExistir() {
        Optional<Abrigo> encontrado = abrigoRepository.findByNome("Abrigo Teste");
        assertTrue(encontrado.isPresent());
        assertEquals(abrigo.getNome(), encontrado.get().getNome());
    }

    @Test
    void findByNome_RetornaVazio_QuandoNaoExistir() {
        Optional<Abrigo> encontrado = abrigoRepository.findByNome("Outro Abrigo");
        assertFalse(encontrado.isPresent());
    }

    @Test
    void existsByNomeOrTelefoneOrEmail_RetornaTrue_QuandoExistir() {
        boolean existe = abrigoRepository.existsByNomeOrTelefoneOrEmail(
                "Abrigo Teste", "12345-6789", "email@test.com");
        assertTrue(existe);
    }

    @Test
    void existsByNomeOrTelefoneOrEmail_RetornaFalse_QuandoNaoExistir() {
        boolean existe = abrigoRepository.existsByNomeOrTelefoneOrEmail(
                "Outro Abrigo", "99999-9999", "outro@email.com");
        assertFalse(existe);
    }
}
