package br.com.alura.adopet.api.api.dto.adocaoDTO;

import jakarta.validation.constraints.NotBlank;

//DTO usado para Reprovar tentativa de adoção, vem do AdocaoController.
public record ReprovarAdocaoDTO(
        @NotBlank(message = "A justificativa é obrigatória.")
        String justificativa
) {}
