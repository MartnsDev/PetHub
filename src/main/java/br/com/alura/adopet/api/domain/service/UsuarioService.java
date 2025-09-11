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

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    // Autenticação/Login
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new ValidacaoException("Usuário ou senha inválidos"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new ValidacaoException("Usuário ou senha inválidos");
        }

        return usuario;
    }

    // Cadastro
    @Transactional
    public void cadastrar(CadastrarUsuarioDTO dto) {
        // Aqui você pode verificar se o email já existe
        if (repository.existsByEmail(dto.email())) {
            throw new ValidacaoException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha())); // Senha criptografada

        repository.save(usuario);
    }
}
