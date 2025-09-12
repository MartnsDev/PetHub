package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.api.dto.loginDTO.CadastrarUsuarioDTO;
import br.com.alura.adopet.api.domain.model.Usuario;
import br.com.alura.adopet.api.domain.repository.UsuarioRepository;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder);
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {
        // Arrange
        CadastrarUsuarioDTO dto = new CadastrarUsuarioDTO("João", "joao@email.com", "123456");
        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.senha())).thenReturn("senhaCriptografada");

        // Act
        usuarioService.cadastrar(dto);

        // Assert
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        Usuario usuarioSalvo = captor.getValue();
        assertEquals("João", usuarioSalvo.getNome());
        assertEquals("joao@email.com", usuarioSalvo.getEmail());
        assertEquals("senhaCriptografada", usuarioSalvo.getSenha());
    }

    @Test
    void deveLancarExcecaoAoCadastrarEmailJaExistente() {
        CadastrarUsuarioDTO dto = new CadastrarUsuarioDTO("João", "joao@email.com", "123456");
        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(ValidacaoException.class, () -> usuarioService.cadastrar(dto));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveAutenticarUsuarioComSucesso() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senhaCriptografada");

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "senhaCriptografada")).thenReturn(true);

        // Act
        Usuario autenticado = usuarioService.autenticar("joao@email.com", "123456");

        // Assert
        assertNotNull(autenticado);
        assertEquals(usuario.getEmail(), autenticado.getEmail());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () -> usuarioService.autenticar("joao@email.com", "123456"));
    }

    @Test
    void deveLancarExcecaoQuandoSenhaInvalida() {
        Usuario usuario = new Usuario();
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senhaCriptografada");

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123456", "senhaCriptografada")).thenReturn(false);

        assertThrows(ValidacaoException.class, () -> usuarioService.autenticar("joao@email.com", "123456"));
    }
}
