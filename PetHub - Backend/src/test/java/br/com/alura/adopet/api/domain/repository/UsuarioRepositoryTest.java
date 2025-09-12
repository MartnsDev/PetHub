package br.com.alura.adopet.api.domain.repository;

import br.com.alura.adopet.api.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setNome("Usuário Teste");
        usuario.setEmail("usuario@test.com");
        usuario.setSenha("123456"); // supondo que tenha senha
        usuarioRepository.save(usuario);
    }

    @Test
    void deveEncontrarUsuarioPorEmail() {
        Optional<Usuario> resultado = usuarioRepository.findByEmail("usuario@test.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("usuario@test.com");
    }

    @Test
    void naoDeveEncontrarUsuarioPorEmailInexistente() {
        Optional<Usuario> resultado = usuarioRepository.findByEmail("inexistente@test.com");

        assertThat(resultado).isNotPresent();
    }

    @Test
    void deveRetornarTrueQuandoEmailJaExiste() {
        boolean existe = usuarioRepository.existsByEmail("usuario@test.com");

        assertThat(existe).isTrue();
    }

    @Test
    void deveRetornarFalseQuandoEmailNaoExiste() {
        boolean existe = usuarioRepository.existsByEmail("novo@test.com");

        assertThat(existe).isFalse();
    }
}
