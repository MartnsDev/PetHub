package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.adocaoDTO.ReprovarAdocaoDTO;
import br.com.alura.adopet.api.api.dto.adocaoDTO.SolicitacaoAdocaoDTO;
import br.com.alura.adopet.api.domain.service.AdocaoService;
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

class AdocaoControllerTest {

    @Mock
    private AdocaoService adocaoService;

    @InjectMocks
    private AdocaoController adocaoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void solicitarAdoção_Sucesso() {
        SolicitacaoAdocaoDTO dto = new SolicitacaoAdocaoDTO(1L, 1L, "Porque sim"); // ajuste conforme campos do seu DTO

        ResponseEntity<String> response = adocaoController.solicitar(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Solicitação de adoção registrada com sucesso!", response.getBody());
        verify(adocaoService, times(1)).solicitar(dto);
    }

    @Test
    void aprovarAdoção_Sucesso() {
        Long id = 1L;

        ResponseEntity<String> response = adocaoController.aprovar(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Adoção aprovada com sucesso!", response.getBody());
        verify(adocaoService, times(1)).aprovar(id);
    }

    @Test
    void reprovarAdoção_Sucesso() {
        Long id = 1L;
        ReprovarAdocaoDTO dto = new ReprovarAdocaoDTO("Justificativa da reprovação");

        ResponseEntity<String> response = adocaoController.reprovar(id, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Adoção reprovada com sucesso!", response.getBody());
        verify(adocaoService, times(1)).reprovar(id, dto.justificativa());
    }

    @Test
    void reprovarAdoção_ComErro() {
        Long id = 99L;
        ReprovarAdocaoDTO dto = new ReprovarAdocaoDTO("Justificativa inválida");

        doThrow(new ValidacaoException("Adoção não encontrada")).when(adocaoService).reprovar(id, dto.justificativa());

        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> adocaoController.reprovar(id, dto)
        );

        assertEquals("Adoção não encontrada", exception.getMessage());
        verify(adocaoService, times(1)).reprovar(id, dto.justificativa());
    }
}
