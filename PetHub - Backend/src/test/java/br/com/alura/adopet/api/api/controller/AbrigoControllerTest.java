package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.abrigoDTO.AbrigoDTO;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarAbrigoDTO;
import br.com.alura.adopet.api.api.dto.petDTO.PetDTO;
import br.com.alura.adopet.api.domain.model.enums.TipoPet;
import br.com.alura.adopet.api.domain.service.AbrigoService;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AbrigoControllerTest {

    @Mock
    private AbrigoService abrigoService;

    @InjectMocks
    private AbrigoController abrigoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarTodosOsAbrigosComOsPets() {
        List<AbrigoDTO> mockList = List.of(
                new AbrigoDTO(1L, "Abrigo Teste", "123456789", "email@test.com", new ArrayList<>())
        );

        when(abrigoService.listarTodosOsAbrigos()).thenReturn(mockList);

        ResponseEntity<List<AbrigoDTO>> response = abrigoController.listarTodosOsAbrigosComOsPets();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockList, response.getBody());
        verify(abrigoService, times(1)).listarTodosOsAbrigos();
    }

    @Test
    void cadastrar() {
        CadastrarAbrigoDTO dto = new CadastrarAbrigoDTO("Abrigo Teste", "email@test.com", "12345-6789");

        ResponseEntity<Void> response = abrigoController.cadastrar(dto);

        assertEquals(201, response.getStatusCodeValue());
        verify(abrigoService, times(1)).cadastrar(dto);
    }

    @Test
    void cadastrarAbrigoJaExistente() {
        CadastrarAbrigoDTO dto = new CadastrarAbrigoDTO("Abrigo Teste", "email@test.com", "12345-6789");
        doThrow(new ValidacaoException("Dados já cadastrados em outro abrigo"))
                .when(abrigoService).cadastrar(dto);

        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> abrigoController.cadastrar(dto)
        );

        assertEquals("Dados já cadastrados em outro abrigo", exception.getMessage());
        verify(abrigoService, times(1)).cadastrar(dto);
    }

    @Test
    void buscarAbrigosComPets() {
        AbrigoDTO abrigoDTO = new AbrigoDTO(1L, "Abrigo Teste", "12345-6789", "email@test.com", new ArrayList<>());
        when(abrigoService.getAbrigo(1L)).thenReturn(abrigoDTO);

        ResponseEntity<AbrigoDTO> response = abrigoController.buscarAbrigosComPets(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(abrigoDTO, response.getBody());
        verify(abrigoService, times(1)).getAbrigo(1L);
    }

    @Test
    void listarPetsDeUmAbrigo() {
        List<PetDTO> petsMock = List.of(
                new PetDTO(
                        1L,                  // id
                        "Rex",               // nome
                        "Vira-lata",         // raca
                        3,                   // idade
                        TipoPet.CACHORRO,    // tipo
                        "Marrom",            // cor
                        new BigDecimal("12.5") // peso
                )
        );

        // Use "1" como String
        when(abrigoService.listarPets("1")).thenReturn(petsMock);

        ResponseEntity<List<PetDTO>> response = abrigoController.listarPetsDeUmAbrigo("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(petsMock, response.getBody());

        // Verifique a chamada com String
        verify(abrigoService, times(1)).listarPets("1");
    }



    @Test
    void deletar() {
        ResponseEntity<Void> response = abrigoController.deletar(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(abrigoService, times(1)).deletar(1L);
    }

    @Test
    void deletarAbrigoNaoExistente() {
        doThrow(new ValidacaoException("Abrigo não encontrado")).when(abrigoService).deletar(99L);

        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> abrigoController.deletar(99L)
        );

        assertEquals("Abrigo não encontrado", exception.getMessage());
        verify(abrigoService, times(1)).deletar(99L);
    }
}
