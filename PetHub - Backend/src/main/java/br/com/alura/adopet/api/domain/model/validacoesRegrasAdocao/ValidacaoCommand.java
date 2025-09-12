package br.com.alura.adopet.api.domain.model.validacoesRegrasAdocao;

import br.com.alura.adopet.api.api.dto.adocaoDTO.SolicitacaoAdocaoDTO;

public interface ValidacaoCommand {
    void validar(SolicitacaoAdocaoDTO dto);
}
