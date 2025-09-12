package br.com.alura.adopet.api.api.dto.tutorDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CadastrarTutorDTO(
        @NotBlank
        String nome,
        @NotBlank
        @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "Telefone deve ter 11 dígitos (DDD + número)")
        String telefone,
        @NotBlank
        @Email
        String email
) {}
