package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarPetDTO;
import br.com.alura.adopet.api.domain.model.Abrigo;
import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.domain.model.enums.TipoPet;
import br.com.alura.adopet.api.domain.repository.AbrigoRepository;
import br.com.alura.adopet.api.domain.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetServiceTest {

    private PetRepository petRepository;
    private AbrigoRepository abrigoRepository;
    private PetService petService;

    @BeforeEach
    void setUp() {
        petRepository = mock(PetRepository.class);
        abrigoRepository = mock(AbrigoRepository.class);
        petService = new PetService(petRepository, abrigoRepository);
    }

    @Test
    void deveListarTodosPetsDisponiveis() {
        // Arrange
        Pet pet1 = new Pet();
        pet1.setNome("Rex");
        Pet pet2 = new Pet();
        pet2.setNome("Luna");

        when(petRepository.findByAdotadoFalse()).thenReturn(List.of(pet1, pet2));

        // Act
        List<Pet> petsDisponiveis = petService.listarTodosDisponiveis();

        // Assert
        assertEquals(2, petsDisponiveis.size());
        verify(petRepository).findByAdotadoFalse();
    }

    @Test
    void deveCadastrarPetComSucesso() {
        // Arrange
        Abrigo abrigo = new Abrigo();
        abrigo.setId(1L);
        when(abrigoRepository.findById(1L)).thenReturn(Optional.of(abrigo));

        CadastrarPetDTO dto = new CadastrarPetDTO(
                "Rex", "SRD", 3, TipoPet.CACHORRO, "médio", BigDecimal.valueOf(20), 1L
        );

        // Act
        petService.cadastrarPet(1L, dto);

        // Assert
        ArgumentCaptor<Pet> captor = ArgumentCaptor.forClass(Pet.class);
        verify(petRepository).save(captor.capture());

        Pet petSalvo = captor.getValue();
        assertEquals("Rex", petSalvo.getNome());
        assertEquals("SRD", petSalvo.getRaca());
        assertEquals(3, petSalvo.getIdade());
        assertEquals(TipoPet.CACHORRO, petSalvo.getTipo());
        assertEquals("médio", petSalvo.getCor());
        assertEquals(BigDecimal.valueOf(20), petSalvo.getPeso());
        assertEquals(abrigo, petSalvo.getAbrigo());
        assertFalse(petSalvo.getAdotado());
    }

    @Test
    void deveLancarExcecaoQuandoAbrigoNaoEncontrado() {
        // Arrange
        when(abrigoRepository.findById(1L)).thenReturn(Optional.empty());
        CadastrarPetDTO dto = new CadastrarPetDTO(
                "Rex", "SRD", 3, TipoPet.CACHORRO, "médio", BigDecimal.valueOf(20), 1L
        );

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> petService.cadastrarPet(1L, dto));
        verify(petRepository, never()).save(any());
    }
}
