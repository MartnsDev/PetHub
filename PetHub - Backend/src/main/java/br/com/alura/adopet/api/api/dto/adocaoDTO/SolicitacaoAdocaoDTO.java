package br.com.alura.adopet.api.api.dto.adocaoDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// DTO para Solicitção de adocão no AdocaoController
public record SolicitacaoAdocaoDTO(
        @NotNull(message = "Id pet precisa ser informado")
        Long idPet,
        @NotNull(message = "Id Tutor precisa ser informado")
        Long idTutor,
        @NotBlank(message = "motivo da adoção precisa ser informado")
        String motivo
) {}
