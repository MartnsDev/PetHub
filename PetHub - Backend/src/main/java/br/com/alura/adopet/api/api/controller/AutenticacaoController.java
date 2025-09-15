package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.loginDTO.CadastrarUsuarioDTO;
import br.com.alura.adopet.api.api.dto.loginDTO.LoginUsuarioDTO;
import br.com.alura.adopet.api.api.dto.loginDTO.TokenUsuarioDTO;
import br.com.alura.adopet.api.domain.model.Usuario;
import br.com.alura.adopet.api.domain.service.UsuarioService;
import br.com.alura.adopet.api.infra.exceptions.ApiResponse;
import br.com.alura.adopet.api.infra.jwt.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth") // Define a rota base para autenticação
@RequiredArgsConstructor // Cria construtor automaticamente para as final fields
public class AutenticacaoController {

    private final UsuarioService usuarioService; // Serviço de usuários
    private final JwtService jwtService;         // Serviço de JWT

    // POST /auth/cadastro -> Cadastra um novo usuário
    @PostMapping("/cadastro")
    public ResponseEntity<ApiResponse> cadastrar(@RequestBody @Valid CadastrarUsuarioDTO dto) {
        usuarioService.cadastrar(dto); // Chama service para criar usuário (pode lançar ValidacaoException)

        // Cria resposta customizada com status de sucesso
        ApiResponse response = new ApiResponse(
                true, // sucesso
                "Usuário cadastrado com sucesso", // mensagem
                null, // dados adicionais (nenhum)
                LocalDateTime.now() // timestamp
        );

        return ResponseEntity.ok(response); // Retorna 200 OK com a resposta
    }

    // POST /auth/login -> Autentica usuário e gera token JWT
    @PostMapping("/login")
    public ResponseEntity<TokenUsuarioDTO> login(@RequestBody LoginUsuarioDTO dto) {
        // Autentica usuário com email e senha
        Usuario usuario = usuarioService.autenticar(dto.email(), dto.senha());

        // Gera token JWT para o usuário autenticado
        String token = jwtService.gerarToken(usuario);

        // Cria DTO de resposta contendo o token e informações do usuário
        TokenUsuarioDTO response = new TokenUsuarioDTO(
                token,                  // token JWT
                usuario.getEmail(),     // email do usuário
                usuario.getNome(),      // nome do usuário
                "Login realizado com sucesso!" // mensagem de sucesso
        );

        return ResponseEntity.ok(response); // Retorna 200 OK com o token
    }

}
