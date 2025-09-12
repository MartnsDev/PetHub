package br.com.alura.adopet.api.domain.model.validacoesRegrasAdocao;

import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import br.com.alura.adopet.api.api.dto.adocaoDTO.SolicitacaoAdocaoDTO;
import br.com.alura.adopet.api.domain.model.enums.StatusAdocao;
import br.com.alura.adopet.api.domain.repository.AdocaoRepository;
import org.springframework.stereotype.Component;



@Component
public class ValidacaoTutorComLimiteDeAdocao implements ValidacaoCommand {

    private final AdocaoRepository adocaoRepository;

    public ValidacaoTutorComLimiteDeAdocao(AdocaoRepository adocaoRepository) {
        this.adocaoRepository = adocaoRepository;
    }

    @Override
    public void validar(SolicitacaoAdocaoDTO dto) {
        long totalAprovadas = adocaoRepository.countByTutorIdAndStatus(
                dto.idTutor(),
                StatusAdocao.APROVADO
        );

        if (totalAprovadas >= 5) {
            throw new ValidacaoException("Tutor chegou ao limite máximo de 5 adoções!");
        }
    }
}
