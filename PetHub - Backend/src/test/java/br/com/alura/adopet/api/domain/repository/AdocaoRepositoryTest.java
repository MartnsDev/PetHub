package br.com.alura.adopet.api.domain.repository;

import br.com.alura.adopet.api.domain.model.Abrigo;
import br.com.alura.adopet.api.domain.model.Adocao;
import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.domain.model.Tutor;
import br.com.alura.adopet.api.domain.model.enums.StatusAdocao;
import br.com.alura.adopet.api.domain.model.enums.TipoPet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AdocaoRepositoryTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private AbrigoRepository abrigoRepository;

    @Autowired
    private AdocaoRepository adocaoRepository;

    private Abrigo abrigo;
    private Tutor tutor;
    private Pet pet;

    @BeforeEach
    void setUp() {
        abrigo = new Abrigo();
        abrigo.setNome("Abrigo Teste");
        abrigo.setEmail("abrigo@test.com");
        abrigo.setTelefone("123456789");
        abrigo = abrigoRepository.save(abrigo);

        tutor = new Tutor();
        tutor.setNome("Tutor Teste");
        tutor.setEmail("tutor@test.com");
        tutor.setTelefone("987654321");
        tutor = tutorRepository.save(tutor);

        pet = new Pet();
        pet.setNome("Pet Teste");
        pet.setIdade(2);
        pet.setCor("Marrom");
        pet.setPeso(BigDecimal.valueOf(5.0));
        pet.setRaca("Vira-lata");
        pet.setTipo(TipoPet.CACHORRO);
        pet.setAdotado(false);
        pet.setAbrigo(abrigo);
        pet = petRepository.save(pet);
    }

    @Test
    void deveCadastrarAdocaoComSucesso() {
        Adocao adocao = new Adocao();
        adocao.setPet(pet);
        adocao.setTutor(tutor);
        adocao.setData(LocalDateTime.now());
        adocao.setStatus(StatusAdocao.AGUARDANDO_AVALIACAO);
        adocao.setJustificativaStatus("Teste");
        adocao.setMotivo("Adoção teste");

        Adocao adocaoSalva = adocaoRepository.save(adocao);

        assertThat(adocaoSalva.getId()).isNotNull();
        assertThat(adocaoSalva.getPet()).isEqualTo(pet);
        assertThat(adocaoSalva.getTutor()).isEqualTo(tutor);
    }

    @Test
    void deveVerificarSeTutorPossuiAdocaoComStatus() {
        Adocao adocao = criarAdocao(StatusAdocao.APROVADO);
        adocaoRepository.save(adocao);

        boolean existe = adocaoRepository.existsByTutorIdAndStatus(tutor.getId(), StatusAdocao.APROVADO);
        assertThat(existe).isTrue();
    }

    @Test
    void deveVerificarSePetPossuiAdocaoComStatus() {
        Adocao adocao = criarAdocao(StatusAdocao.APROVADO);
        adocaoRepository.save(adocao);

        boolean existe = adocaoRepository.existsByPetIdAndStatus(pet.getId(), StatusAdocao.APROVADO);
        assertThat(existe).isTrue();
    }

    @Test
    void deveContarAdocoesPorTutorEStatus() {
        Adocao adocao1 = criarAdocao(StatusAdocao.APROVADO);
        Adocao adocao2 = criarAdocao(StatusAdocao.APROVADO);
        adocaoRepository.save(adocao1);
        adocaoRepository.save(adocao2);

        long count = adocaoRepository.countByTutorIdAndStatus(tutor.getId(), StatusAdocao.APROVADO);
        assertThat(count).isEqualTo(2);
    }

    // método auxiliar para criar adocao preenchendo todos os campos obrigatórios
    private Adocao criarAdocao(StatusAdocao status) {
        Adocao adocao = new Adocao();
        adocao.setPet(pet);
        adocao.setTutor(tutor);
        adocao.setData(LocalDateTime.now());
        adocao.setStatus(status);
        adocao.setJustificativaStatus("Teste");
        adocao.setMotivo("Motivo teste");
        return adocao;
    }
}
