package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.loginDTO.CadastrarUsuarioDTO;
import br.com.alura.adopet.api.api.dto.loginDTO.LoginUsuarioDTO;
import br.com.alura.adopet.api.api.dto.loginDTO.TokenUsuarioDTO;
import br.com.alura.adopet.api.domain.model.Usuario;
import br.com.alura.adopet.api.domain.service.UsuarioService;
import br.com.alura.adopet.api.infra.exceptions.ApiResponse;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import br.com.alura.adopet.api.infra.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutenticacaoControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AutenticacaoController autenticacaoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- TESTE DE CADASTRO ----------

    @Test
    void cadastrar_Sucesso() {
        CadastrarUsuarioDTO dto = new CadastrarUsuarioDTO("Matheus", "teste@email.com", "123456");

        ResponseEntity<ApiResponse> response = autenticacaoController.cadastrar(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().sucesso());
        assertEquals("Usuário cadastrado com sucesso", response.getBody().mensagem());

        verify(usuarioService, times(1)).cadastrar(dto);
    }

    @Test
    void cadastrar_ComErro() {
        CadastrarUsuarioDTO dto = new CadastrarUsuarioDTO("Matheus", "teste@email.com", "123456");

        doThrow(new ValidacaoException("Email já cadastrado")).when(usuarioService).cadastrar(dto);

        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> autenticacaoController.cadastrar(dto)
        );

        assertEquals("Email já cadastrado", exception.getMessage());
        verify(usuarioService, times(1)).cadastrar(dto);
    }

    // ---------- TESTE DE LOGIN ----------

    @Test
    void login_Sucesso() {
        LoginUsuarioDTO dto = new LoginUsuarioDTO("teste@email.com", "123456");
        Usuario usuarioMock = new Usuario(1L, "Matheus", "teste@email.com", "123456");

        when(usuarioService.autenticar(dto.email(), dto.senha())).thenReturn(usuarioMock);
        when(jwtService.gerarToken(usuarioMock)).thenReturn("token123");

        ResponseEntity<TokenUsuarioDTO> response = autenticacaoController.login(dto);

        assertEquals(200, response.getStatusCodeValue());
        TokenUsuarioDTO body = response.getBody();

        assertNotNull(body);
        assertEquals("token123", body.token());
        assertEquals("Login realizado com sucesso!", body.mensagem());
        assertEquals(usuarioMock.getEmail(), body.email());
        assertEquals(usuarioMock.getNome(), body.nome());

        verify(usuarioService, times(1)).autenticar(dto.email(), dto.senha());
        verify(jwtService, times(1)).gerarToken(usuarioMock);
    }

    @Test
    void login_ComErro() {
        LoginUsuarioDTO dto = new LoginUsuarioDTO("teste@email.com", "123456");

        when(usuarioService.autenticar(dto.email(), dto.senha()))
                .thenThrow(new ValidacaoException("Email ou senha inválidos"));

        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> autenticacaoController.login(dto)
        );

        assertEquals("Email ou senha inválidos", exception.getMessage());
        verify(usuarioService, times(1)).autenticar(dto.email(), dto.senha());
        verifyNoInteractions(jwtService);
    }
}
