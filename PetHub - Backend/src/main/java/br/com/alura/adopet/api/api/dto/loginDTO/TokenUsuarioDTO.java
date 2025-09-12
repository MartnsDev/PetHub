package br.com.alura.adopet.api.api.dto.loginDTO;

public record TokenUsuarioDTO(
        String token,
        String email,
        String nome,
        String mensagem
) {}