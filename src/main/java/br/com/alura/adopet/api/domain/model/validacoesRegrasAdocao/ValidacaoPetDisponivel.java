package br.com.alura.adopet.api.domain.model.validacoesRegrasAdocao;

import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import br.com.alura.adopet.api.domain.repository.PetRepository;
import br.com.alura.adopet.api.api.dto.adocaoDTO.SolicitacaoAdocaoDTO;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoPetDisponivel implements ValidacaoCommand {

    private final PetRepository petRepository;

    public ValidacaoPetDisponivel(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public void validar(SolicitacaoAdocaoDTO dto) {
        Boolean adotado = petRepository.isAdotado(dto.idPet());
        if (adotado == null) {
            throw new ValidacaoException("Pet não encontrado!");
        }
        if (adotado) {
            throw new ValidacaoException("Pet já foi adotado!");
        }
    }
}
