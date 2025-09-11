package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.tutorDTO.AtualizarTutorDTO;
import br.com.alura.adopet.api.api.dto.tutorDTO.CadastrarTutorDTO;
import br.com.alura.adopet.api.domain.service.TutorService;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TutorControllerTest {

    @Mock
    private TutorService tutorService;

    @InjectMocks
    private TutorController tutorController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cadastrarTutor_Sucesso() {
        CadastrarTutorDTO dto = new CadastrarTutorDTO("João", "joao@email.com", "12345-6789");

        ResponseEntity<String> response = tutorController.cadastrar(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Tutor cadastrado com sucesso!", response.getBody());
        verify(tutorService, times(1)).cadastrar(dto);
    }

    @Test
    void cadastrarTutor_ComErro() {
        CadastrarTutorDTO dto = new CadastrarTutorDTO("João", "joao@email.com", "12345-6789");

        doThrow(new ValidacaoException("Tutor já existe")).when(tutorService).cadastrar(dto);

        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> tutorController.cadastrar(dto)
        );

        assertEquals("Tutor já existe", exception.getMessage());
        verify(tutorService, times(1)).cadastrar(dto);
    }

    @Test
    void atualizarTutor_Sucesso() {
        AtualizarTutorDTO dto = new AtualizarTutorDTO(1L, "João Atualizado", "joao@novoemail.com", "98765-4321");

        ResponseEntity<String> response = tutorController.atualizar(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Tutor atualizado com sucesso!", response.getBody());
        verify(tutorService, times(1)).atualizar(dto);
    }

    @Test
    void atualizarTutor_ComErro() {
        AtualizarTutorDTO dto = new AtualizarTutorDTO(99L, "João", "joao@email.com", "12345-6789");

        doThrow(new ValidacaoException("Tutor não encontrado")).when(tutorService).atualizar(dto);

        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> tutorController.atualizar(dto)
        );

        assertEquals("Tutor não encontrado", exception.getMessage());
        verify(tutorService, times(1)).atualizar(dto);
    }
}
