package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarPetDTO;
import br.com.alura.adopet.api.domain.model.enums.TipoPet;
import br.com.alura.adopet.api.domain.service.PetService;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PetControllerTest {

    @Mock
    private PetService petService;

    @InjectMocks
    private PetController petController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarTodosDisponiveis_Sucesso() {
        // Criando pets simulados com todos os campos necessários
        Pet pet1 = new Pet(1L, TipoPet.CACHORRO, "Rex", "Vira-lata", 3, "Marrom", new BigDecimal("12.5"), false, null, null);
        Pet pet2 = new Pet(2L, TipoPet.GATO, "Mia", "Siamês", 2, "Branco", new BigDecimal("4.3"), false, null, null);

        List<Pet> petsMock = List.of(pet1, pet2);

        when(petService.listarTodosDisponiveis()).thenReturn(petsMock);

        ResponseEntity<List<Pet>> response = petController.listarTodosDisponiveis();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(petsMock, response.getBody());
        verify(petService, times(1)).listarTodosDisponiveis();
    }

    @Test
    void cadastrarPet_Sucesso() {
        Long idAbrigo = 1L;
        CadastrarPetDTO dto = new CadastrarPetDTO(
                "Rex",                  // nome
                "Vira-lata",            // raca
                3,                      // idade
                TipoPet.CACHORRO,       // tipo
                "Marrom",               // cor
                new BigDecimal("12.5"), // peso
                idAbrigo                // idAbrigo
        );

        ResponseEntity<Void> response = petController.cadastrarPet(idAbrigo, dto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("/abrigos/" + idAbrigo + "/pets", response.getHeaders().getLocation().toString());
        verify(petService, times(1)).cadastrarPet(idAbrigo, dto);
    }


    @Test
    void cadastrarPet_ComErro() {
        Long idAbrigo = 99L;
        CadastrarPetDTO dto = new CadastrarPetDTO(
                "Rex",                   // nome
                "Vira-lata",             // raca
                3,                       // idade
                TipoPet.CACHORRO,        // tipo
                "Marrom",                // cor
                new BigDecimal("12.5"),  // peso
                idAbrigo                 // idAbrigo
        );

        doThrow(new ValidacaoException("Abrigo não encontrado")).when(petService).cadastrarPet(idAbrigo, dto);

        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> petController.cadastrarPet(idAbrigo, dto)
        );

        assertEquals("Abrigo não encontrado", exception.getMessage());
        verify(petService, times(1)).cadastrarPet(idAbrigo, dto);
    }

}
