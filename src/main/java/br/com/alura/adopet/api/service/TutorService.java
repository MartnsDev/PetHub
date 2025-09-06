package br.com.alura.adopet.api.service;

import br.com.alura.adopet.api.infra.ValidacaoException;
import br.com.alura.adopet.api.model.Tutor;
import br.com.alura.adopet.api.repository.TutorRepository;
import org.springframework.stereotype.Service;

@Service
public class TutorService {

    private final TutorRepository repository;

    public TutorService(TutorRepository repository) {
        this.repository = repository;
    }

    public void cadastrar(Tutor tutor) {
        boolean telefoneJaCadastrado = repository.existsByTelefone(tutor.getTelefone());
        boolean emailJaCadastrado = repository.existsByEmail(tutor.getEmail());

        if (telefoneJaCadastrado || emailJaCadastrado) {
            throw new ValidacaoException("Dados já cadastrados para outro tutor!");
        }
        repository.save(tutor);
    }

    public void atualizar(Tutor tutor) {
        if (!repository.existsById(tutor.getId())) {
            throw new ValidacaoException("Tutor não encontrado!");
        }
        repository.save(tutor);
    }
}

