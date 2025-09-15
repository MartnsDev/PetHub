package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.domain.model.Usuario;
import br.com.alura.adopet.api.api.dto.loginDTO.CadastrarUsuarioDTO;
import br.com.alura.adopet.api.domain.repository.UsuarioRepository;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;       // Repositório de usuários
    private final PasswordEncoder passwordEncoder;    // Criptografia de senhas

    /**
     * Autentica um usuário pelo email e senha.
     * @param email Email do usuário
     * @param senha Senha em texto plano
     * @return Usuario autenticado
     * @throws ValidacaoException caso email não exista ou senha esteja incorreta
     */
    public Usuario autenticar(String email, String senha) {
        // Busca usuário pelo email
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new ValidacaoException("Email ou senha incorretos"));

        // Verifica se a senha informada bate com a senha criptografada
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new ValidacaoException("Email ou senha incorretos");
        }

        return usuario;
    }


    /**
     * Cadastra um novo usuário.
     * @param dto Dados do usuário vindos do frontend
     * @throws ValidacaoException caso o email já exista
     */
    @Transactional
    public void cadastrar(CadastrarUsuarioDTO dto) {
        // Verifica se o email já está cadastrado
        if (repository.existsByEmail(dto.email())) {
            throw new ValidacaoException("Email já cadastrado");
        }

        // Cria novo usuário e criptografa a senha
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));

        // Salva no banco de dados
        repository.save(usuario);
    }

    /**
     * Converte um DTO de cadastro para a entidade Usuario
     * @param dto Dados do cadastro
     * @return Usuario
     */
    private Usuario fromDTO(CadastrarUsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        return usuario;
    }
}
