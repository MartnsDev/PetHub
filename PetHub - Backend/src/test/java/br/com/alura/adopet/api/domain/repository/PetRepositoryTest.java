package br.com.alura.adopet.api.domain.repository;

import br.com.alura.adopet.api.domain.model.Abrigo;
import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.domain.model.enums.TipoPet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PetRepositoryTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private AbrigoRepository abrigoRepository;

    private Abrigo abrigo;

    @BeforeEach
    void setUp() {
        abrigo = new Abrigo();
        abrigo.setNome("Abrigo Teste");
        abrigo.setEmail("abrigo@test.com");
        abrigo.setTelefone("123456789");
        abrigo = abrigoRepository.save(abrigo);
    }

    private Pet criarPet(String nome, boolean adotado) {
        Pet pet = new Pet();
        pet.setNome(nome);
        pet.setIdade(3);
        pet.setCor("Preto");
        pet.setPeso(BigDecimal.valueOf(10));
        pet.setRaca("SRD");
        pet.setTipo(TipoPet.CACHORRO);
        pet.setAdotado(adotado);
        pet.setAbrigo(abrigo);
        return petRepository.save(pet);
    }

    @Test
    void deveRetornarPetsNaoAdotados() {
        // Arrange
        Pet pet1 = criarPet("Rex", false);
        Pet pet2 = criarPet("Luna", false);
        Pet pet3 = criarPet("Bidu", true);

        // Act
        List<Pet> petsNaoAdotados = petRepository.findByAdotadoFalse();

        // Assert
        assertThat(petsNaoAdotados)
                .isNotEmpty()
                .contains(pet1, pet2)
                .doesNotContain(pet3);
    }

    @Test
    void deveVerificarSePetEstaAdotado() {
        // Arrange
        Pet petNaoAdotado = criarPet("Mia", false);
        Pet petAdotado = criarPet("Thor", true);

        // Act
        Boolean statusNaoAdotado = petRepository.isAdotado(petNaoAdotado.getId());
        Boolean statusAdotado = petRepository.isAdotado(petAdotado.getId());

        // Assert
        assertThat(statusNaoAdotado).isFalse();
        assertThat(statusAdotado).isTrue();
    }
}
