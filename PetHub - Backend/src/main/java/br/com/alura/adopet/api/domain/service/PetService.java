package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.domain.model.Abrigo;
import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarPetDTO;
import br.com.alura.adopet.api.domain.repository.AbrigoRepository;
import br.com.alura.adopet.api.domain.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final AbrigoRepository abrigoRepository;

    public PetService(PetRepository petRepository,
                      AbrigoRepository abrigoRepository) {
        this.petRepository = petRepository;
        this.abrigoRepository = abrigoRepository;
    }

    public List<Pet> listarTodosDisponiveis() {
        return petRepository.findByAdotadoFalse(); // consulta direto no banco
    }

    @Transactional
    public void cadastrarPet(Long idAbrigo, CadastrarPetDTO dto) {
        Abrigo abrigo = abrigoRepository.findById(idAbrigo)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado"));

        Pet pet = new Pet();
        pet.setNome(dto.nome());
        pet.setRaca(dto.raca());
        pet.setIdade(dto.idade());
        pet.setTipo(dto.tipo());
        pet.setCor(dto.cor());
        pet.setPeso(dto.peso());
        pet.setAbrigo(abrigo);
        pet.setAdotado(false);

        petRepository.save(pet);
    }
}
