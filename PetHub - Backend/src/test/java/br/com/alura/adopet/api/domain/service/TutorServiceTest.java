package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.api.dto.tutorDTO.AtualizarTutorDTO;
import br.com.alura.adopet.api.api.dto.tutorDTO.CadastrarTutorDTO;
import br.com.alura.adopet.api.domain.model.Tutor;
import br.com.alura.adopet.api.domain.repository.TutorRepository;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TutorServiceTest {

    private TutorRepository tutorRepository;
    private TutorService tutorService;

    @BeforeEach
    void setUp() {
        tutorRepository = mock(TutorRepository.class);
        tutorService = new TutorService(tutorRepository);
    }

    @Test
    void deveCadastrarTutorComSucesso() {
        // Arrange
        CadastrarTutorDTO dto = new CadastrarTutorDTO("João", "11999999999", "joao@email.com");
        when(tutorRepository.existsByEmailOrTelefone(dto.email(), dto.telefone())).thenReturn(false);

        // Act
        tutorService.cadastrar(dto);

        // Assert
        ArgumentCaptor<Tutor> captor = ArgumentCaptor.forClass(Tutor.class);
        verify(tutorRepository).save(captor.capture());

        Tutor tutorSalvo = captor.getValue();
        assertEquals("João", tutorSalvo.getNome());
        assertEquals("11999999999", tutorSalvo.getTelefone());
        assertEquals("joao@email.com", tutorSalvo.getEmail());
    }

    @Test
    void deveLancarExcecaoAoCadastrarTutorJaExistente() {
        // Arrange
        CadastrarTutorDTO dto = new CadastrarTutorDTO("João", "11999999999", "joao@email.com");
        when(tutorRepository.existsByEmailOrTelefone(dto.email(), dto.telefone())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidacaoException.class, () -> tutorService.cadastrar(dto));
        verify(tutorRepository, never()).save(any());
    }

    @Test
    void deveAtualizarTutorComSucesso() {
        // Arrange
        Tutor tutorExistente = new Tutor();
        tutorExistente.setId(1L);
        tutorExistente.setNome("Maria");
        tutorExistente.setTelefone("1133445566");
        tutorExistente.setEmail("maria@email.com");

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutorExistente));

        AtualizarTutorDTO dto = new AtualizarTutorDTO(1L, "Maria Atualizada", "11999988877", "mariaatualizada@email.com");

        // Act
        tutorService.atualizar(dto);

        // Assert
        ArgumentCaptor<Tutor> captor = ArgumentCaptor.forClass(Tutor.class);
        verify(tutorRepository).save(captor.capture());

        Tutor tutorAtualizado = captor.getValue();
        assertEquals("Maria Atualizada", tutorAtualizado.getNome());
        assertEquals("11999988877", tutorAtualizado.getTelefone());
        assertEquals("mariaatualizada@email.com", tutorAtualizado.getEmail());
    }

    @Test
    void deveLancarExcecaoAoAtualizarTutorNaoExistente() {
        // Arrange
        AtualizarTutorDTO dto = new AtualizarTutorDTO(1L, "Maria", "1133445566", "maria@email.com");
        when(tutorRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidacaoException.class, () -> tutorService.atualizar(dto));
        verify(tutorRepository, never()).save(any());
    }
}
