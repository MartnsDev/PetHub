package br.com.alura.adopet.api.service;

import br.com.alura.adopet.api.infra.ValidacaoException;
import br.com.alura.adopet.api.model.Abrigo;
import br.com.alura.adopet.api.model.Pet;
import br.com.alura.adopet.api.model.dto.abrigoDTO.CadastrarAbrigoDTO;
import br.com.alura.adopet.api.repository.AbrigoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbrigoService {

    @Autowired
    private AbrigoRepository repository;

    public List<Abrigo> listar() {
        return repository.findAll();
    }

    @Transactional
    public void cadastrar(CadastrarAbrigoDTO dto) {
        Abrigo abrigo = Abrigo.builder()
                .nome(dto.nome())
                .email(dto.email())
                .telefone(dto.telefone())
                .build();
        repository.save(abrigo);
    }
    public List<Pet> listarPets(String idOuNome) {
        Abrigo abrigo = buscarAbrigoPorIdOuNome(idOuNome);
        return abrigo.getPets();
    }

    @Transactional
    public void cadastrarPet(String idOuNome, Pet pet) {
        Abrigo abrigo = buscarAbrigoPorIdOuNome(idOuNome);
        pet.setAbrigo(abrigo);
        pet.setAdotado(false);
        abrigo.getPets().add(pet);
        repository.save(abrigo);
    }

    private Abrigo buscarAbrigoPorIdOuNome(String idOuNome) {
        try {
            Long id = Long.parseLong(idOuNome);
            return repository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado"));
        } catch (NumberFormatException e) {
            Abrigo abrigo = repository.findByNome(idOuNome);
            if (abrigo == null) {
                throw new EntityNotFoundException("Abrigo não encontrado");
            }
            return abrigo;
        }
    }
}
