package br.com.alura.adopet.api.api.dto.tutorDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarTutorDTO(
        @NotNull Long id,
        @NotBlank String nome,
        @NotBlank String telefone,
        @NotBlank @Email String email
) {}
