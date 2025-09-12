package br.com.alura.adopet.api.domain.model.validacoesRegrasAdocao;

import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import br.com.alura.adopet.api.api.dto.adocaoDTO.SolicitacaoAdocaoDTO;
import br.com.alura.adopet.api.domain.model.enums.StatusAdocao;
import br.com.alura.adopet.api.domain.repository.AdocaoRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoPetComAdocaoEmAndamento implements ValidacaoCommand {

    private final AdocaoRepository adocaoRepository;

    public ValidacaoPetComAdocaoEmAndamento(AdocaoRepository adocaoRepository) {
        this.adocaoRepository = adocaoRepository;
    }

    @Override
    public void validar(SolicitacaoAdocaoDTO dto) {
        boolean petEmAdocao = adocaoRepository.existsByPetIdAndStatus(
                dto.idPet(),
                StatusAdocao.AGUARDANDO_AVALIACAO
        );

        if (petEmAdocao) {
            throw new ValidacaoException("Pet já está aguardando avaliação em outra adoção!");
        }
    }
}
