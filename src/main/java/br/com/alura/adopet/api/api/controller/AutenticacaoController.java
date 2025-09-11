package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.infra.jwt.JwtService;
import br.com.alura.adopet.api.domain.model.Usuario;

import br.com.alura.adopet.api.api.dto.loginDTO.CadastrarUsuarioDTO;
import br.com.alura.adopet.api.api.dto.loginDTO.LoginUsuarioDTO;
import br.com.alura.adopet.api.api.dto.loginDTO.TokenDTO;
import br.com.alura.adopet.api.domain.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    // Cadastro
    @PostMapping("/cadastro")
    public ResponseEntity<String> cadastrar(@RequestBody @Valid CadastrarUsuarioDTO dto) {
        usuarioService.cadastrar(dto);
        return ResponseEntity.ok("Cadastrado com sucesso");
    }


    // Login
// Exemplo de endpoint de login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUsuarioDTO dto) {
        Usuario usuario = usuarioService.autenticar(dto.email(), dto.senha());

        String token = jwtService.gerarToken(usuario); // Aqui você gera o JWT

        return ResponseEntity.ok(new TokenDTO(token, "Login Realizado com Sucesso!"));
    }

}

