package br.com.alura.adopet.api.domain.repository;

import br.com.alura.adopet.api.domain.model.Tutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TutorRepositoryTest {

    @Autowired
    private TutorRepository tutorRepository;

    private Tutor tutor;

    @BeforeEach
    void setUp() {
        tutor = new Tutor();
        tutor.setNome("Tutor Teste");
        tutor.setEmail("tutor@test.com");
        tutor.setTelefone("123456789");
        tutorRepository.save(tutor);
    }

    @Test
    void deveRetornarTrueQuandoEmailJaExiste() {
        boolean existe = tutorRepository.existsByEmailOrTelefone("tutor@test.com", "000000000");
        assertThat(existe).isTrue();
    }

    @Test
    void deveRetornarTrueQuandoTelefoneJaExiste() {
        boolean existe = tutorRepository.existsByEmailOrTelefone("outro@test.com", "123456789");
        assertThat(existe).isTrue();
    }

    @Test
    void deveRetornarFalseQuandoEmailETelefoneNaoExistem() {
        boolean existe = tutorRepository.existsByEmailOrTelefone("novo@test.com", "987654321");
        assertThat(existe).isFalse();
    }
}
