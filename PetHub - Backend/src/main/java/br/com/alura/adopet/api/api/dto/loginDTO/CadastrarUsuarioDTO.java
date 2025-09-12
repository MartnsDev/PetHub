package br.com.alura.adopet.api.api.dto.loginDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CadastrarUsuarioDTO(
        @NotBlank(message = "Nome é obrigatório")
        String nome,
        @Email(message = "Email inválido") @NotBlank(message = "Email é obrigatório")
        String email,
        @NotBlank(message = "Senha é obrigatória")
        String senha
) {}
