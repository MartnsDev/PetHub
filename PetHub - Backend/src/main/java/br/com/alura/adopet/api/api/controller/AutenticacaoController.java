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
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AutenticacaoController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @PostMapping("/cadastro")
    public ResponseEntity<ApiResponse> cadastrar(@RequestBody @Valid CadastrarUsuarioDTO dto) {
        usuarioService.cadastrar(dto); // pode lançar ValidacaoException
        ApiResponse response = new ApiResponse(
                true,
                "Usuário cadastrado com sucesso",
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }



    @PostMapping("/login")
    public ResponseEntity<TokenUsuarioDTO> login(@RequestBody LoginUsuarioDTO dto) {
        Usuario usuario = usuarioService.autenticar(dto.email(), dto.senha());

        String token = jwtService.gerarToken(usuario); // gera o JWT

        TokenUsuarioDTO response = new TokenUsuarioDTO(
                token,
                usuario.getEmail(),
                usuario.getNome(),
                "Login realizado com sucesso!"
        );
        return ResponseEntity.ok(response);
    }


}

