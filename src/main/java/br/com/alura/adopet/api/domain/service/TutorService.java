package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import br.com.alura.adopet.api.domain.model.Tutor;
import br.com.alura.adopet.api.api.dto.tutorDTO.AtualizarTutorDTO;
import br.com.alura.adopet.api.api.dto.tutorDTO.CadastrarTutorDTO;
import br.com.alura.adopet.api.domain.repository.TutorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;

    public TutorService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @Transactional
    public void cadastrar(CadastrarTutorDTO dto) {
        boolean tutorJaCadastrado = tutorRepository.existsByEmailOrTelefone(dto.email(), dto.telefone());

        if (tutorJaCadastrado) {
            throw new ValidacaoException("Dados já cadastrados para outro tutor!");
        }

        Tutor tutor = new Tutor();
        tutor.setNome(dto.nome());
        tutor.setTelefone(dto.telefone());
        tutor.setEmail(dto.email());

        tutorRepository.save(tutor);
    }

    @Transactional
    public void atualizar(AtualizarTutorDTO dto) {
        Tutor tutor = tutorRepository.findById(dto.id())
                .orElseThrow(() -> new ValidacaoException("Tutor não encontrado!"));

        tutor.setNome(dto.nome());
        tutor.setTelefone(dto.telefone());
        tutor.setEmail(dto.email());

        tutorRepository.save(tutor);
    }

}

