package br.com.alura.adopet.api.model.dto.adocaoDTO;

import jakarta.validation.constraints.NotBlank;

public record AprovacaoAdocaoDTO(
        @NotBlank
        String observacao
) {}
