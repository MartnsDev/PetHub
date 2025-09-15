package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.petDTO.PetDTO;
import br.com.alura.adopet.api.domain.model.enums.TipoPet;
import br.com.alura.adopet.api.domain.service.PetService;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        // Arrange
        PetDTO pet1 = new PetDTO(
                1L,
                "Rex",
                "Vira-lata",
                3,
                "Marrom",
                new BigDecimal("12.5"),
                TipoPet.CACHORRO.name(),
                false,
                List.of("rex.jpg"),
                "abrigo@email.com"
        );

        PetDTO pet2 = new PetDTO(
                2L,
                "Mia",
                "Siamês",
                2,
                "Branco",
                new BigDecimal("4.3"),
                TipoPet.GATO.name(),
                false,
                List.of("mia.jpg"),
                "abrigo@email.com"
        );

        List<PetDTO> petsMock = List.of(pet1, pet2);

        when(petService.listarTodosDisponiveisDTO()).thenReturn(petsMock);

        // Act
        ResponseEntity<List<PetDTO>> response = petController.listarTodosDisponiveis();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(petsMock, response.getBody());
        verify(petService, times(1)).listarTodosDisponiveisDTO();
    }

    @Test
    void uploadImagens_Sucesso() {
        // Arrange
        Long idPet = 1L;
        MultipartFile mockFile = mock(MultipartFile.class);
        List<MultipartFile> fotos = List.of(mockFile);

        // Act
        ResponseEntity<Void> response = petController.uploadImagens(idPet, fotos);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(petService, times(1)).salvarImagens(idPet, fotos);
    }

    @Test
    void uploadImagens_ComErro() {
        // Arrange
        Long idPet = 99L;
        MultipartFile mockFile = mock(MultipartFile.class);
        List<MultipartFile> fotos = List.of(mockFile);

        doThrow(new ValidacaoException("Pet não encontrado"))
                .when(petService).salvarImagens(idPet, fotos);

        // Act + Assert
        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> petController.uploadImagens(idPet, fotos)
        );

        assertEquals("Pet não encontrado", exception.getMessage());
        verify(petService, times(1)).salvarImagens(idPet, fotos);
    }
}
