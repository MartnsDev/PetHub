package br.com.alura.adopet.api.model.dto.adocaoDTO;

import jakarta.validation.constraints.NotBlank;

public record ReprovarAdocaoDTO(
        @NotBlank(message = "A justificativa é obrigatória.")
        String justificativa
) {}
