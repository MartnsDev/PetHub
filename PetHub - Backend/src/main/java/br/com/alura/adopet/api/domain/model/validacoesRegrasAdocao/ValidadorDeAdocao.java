package br.com.alura.adopet.api.domain.model.validacoesRegrasAdocao;

import br.com.alura.adopet.api.api.dto.adocaoDTO.SolicitacaoAdocaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidadorDeAdocao {

    private final List<ValidacaoCommand> validacoes;

    @Autowired
    public ValidadorDeAdocao(List<ValidacaoCommand> validacoes) {
        this.validacoes = validacoes;
    }

    public void validar(SolicitacaoAdocaoDTO dto) {
        // Executa cada validação
        validacoes.forEach(v -> v.validar(dto));
    }
}
