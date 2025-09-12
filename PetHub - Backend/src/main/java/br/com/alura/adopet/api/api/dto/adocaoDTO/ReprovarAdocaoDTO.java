package br.com.alura.adopet.api.api.dto.adocaoDTO;

import jakarta.validation.constraints.NotBlank;

public record ReprovarAdocaoDTO(
        @NotBlank(message = "A justificativa é obrigatória.")
        String justificativa
) {}
