package br.com.alura.adopet.api.api.dto.loginDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUsuarioDTO(
        @NotBlank @Email String email,
        @NotBlank String senha
) {}


