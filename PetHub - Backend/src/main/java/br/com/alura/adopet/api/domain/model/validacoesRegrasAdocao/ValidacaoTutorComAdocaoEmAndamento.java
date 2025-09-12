package br.com.alura.adopet.api.domain.model.validacoesRegrasAdocao;

import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import br.com.alura.adopet.api.api.dto.adocaoDTO.SolicitacaoAdocaoDTO;
import br.com.alura.adopet.api.domain.model.enums.StatusAdocao;
import br.com.alura.adopet.api.domain.repository.AdocaoRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoTutorComAdocaoEmAndamento implements ValidacaoCommand {

    private final AdocaoRepository adocaoRepository;

    public ValidacaoTutorComAdocaoEmAndamento(AdocaoRepository adocaoRepository) {
        this.adocaoRepository = adocaoRepository;
    }

    @Override
    public void validar(SolicitacaoAdocaoDTO dto) {
        boolean tutorAguardando = adocaoRepository.existsByTutorIdAndStatus(
                dto.idTutor(),
                StatusAdocao.AGUARDANDO_AVALIACAO
        );

        if (tutorAguardando) {
            throw new ValidacaoException("Tutor já possui outra adoção aguardando avaliação!");
        }
    }
}
