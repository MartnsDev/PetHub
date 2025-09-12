package br.com.alura.adopet.api.api.dto.adocaoDTO;

import jakarta.validation.constraints.NotBlank;

public record AprovacaoAdocaoDTO(
        @NotBlank
        String observacao
) {}
