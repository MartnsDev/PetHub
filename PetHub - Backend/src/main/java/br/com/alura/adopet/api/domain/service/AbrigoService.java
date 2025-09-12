package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.api.dto.abrigoDTO.AbrigoDTO;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarAbrigoDTO;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarPetDTO;
import br.com.alura.adopet.api.api.dto.petDTO.PetDTO;
import br.com.alura.adopet.api.domain.model.Abrigo;
import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.domain.repository.AbrigoRepository;
import br.com.alura.adopet.api.domain.repository.PetRepository;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AbrigoService {

    private final AbrigoRepository abrigoRepository;
    private final PetRepository petRepository;

    public AbrigoService(AbrigoRepository abrigoRepository, PetRepository petRepository) {
        this.abrigoRepository = abrigoRepository;
        this.petRepository = petRepository;
    }

    // Service
    @Transactional(readOnly = true)
    public List<AbrigoDTO> listarTodosOsAbrigos() {
        return abrigoRepository.findAll().stream()
                .map(abrigo -> {
                    // Força o carregamento dos pets
                    List<PetDTO> pets = abrigo.getPets().stream()
                            .map(p -> new PetDTO(
                                    p.getId(),
                                    p.getNome(),
                                    p.getRaca(),
                                    p.getIdade(),
                                    p.getTipo(),
                                    p.getCor(),
                                    p.getPeso()
                            ))
                            .toList();

                    return new AbrigoDTO(
                            abrigo.getId(),
                            abrigo.getNome(),
                            abrigo.getTelefone(),
                            abrigo.getEmail(),
                            pets
                    );
                })
                .toList();
    }

    @Transactional
    public void cadastrar(CadastrarAbrigoDTO dto) {

        // Verifica se já existe
        boolean jaCadastrado = abrigoRepository.existsByNomeOrTelefoneOrEmail(
                dto.nome(), dto.telefone(), dto.email()
        );

        if (jaCadastrado) {
            throw new ValidacaoException("Dados já cadastrados em outro abrigo");
        }

        // Cria e salva
        Abrigo abrigo = Abrigo.builder()
                .nome(dto.nome())
                .email(dto.email())
                .telefone(dto.telefone())
                .build();

        abrigoRepository.save(abrigo);
    }

    @Transactional(readOnly = true)
    public List<PetDTO> listarPets(String idOuNome) {
        // Busca o abrigo por ID ou nome
        Abrigo abrigo = buscarAbrigoPorIdOuNome(idOuNome);

        // Força o carregamento da lista de pets
        List<PetDTO> pets = abrigo.getPets().stream()
                .map(p -> new PetDTO(
                        p.getId(),
                        p.getNome(),
                        p.getRaca(),
                        p.getIdade(),
                        p.getTipo(),
                        p.getCor(),
                        p.getPeso()
                ))
                .toList();

        return pets;
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

    private Abrigo buscarAbrigoPorIdOuNome(String idOuNome) {
        try {
            Long id = Long.parseLong(idOuNome);
            return abrigoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado"));
        } catch (NumberFormatException e) {
            return abrigoRepository.findByNome(idOuNome)
                    .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado"));
        }
    }

    @Transactional
    public void deletar(Long idAbrigo) {
        if (!abrigoRepository.existsById(idAbrigo)) {
            throw new ValidacaoException("Abrigo não encontrado");
        }
        abrigoRepository.deleteById(idAbrigo);
    }


    @Transactional(readOnly = true)
    public AbrigoDTO getAbrigo(Long idAbrigo) {
        Abrigo abrigo = abrigoRepository.findById(idAbrigo)
                .orElseThrow(() -> new ValidacaoException("Abrigo não encontrado"));

        // Aqui você acessa a lista -> o Hibernate vai disparar o SELECT dos pets
        List<PetDTO> pets = abrigo.getPets().stream()
                .map(p -> new PetDTO(
                        p.getId(),
                        p.getNome(),
                        p.getRaca(),
                        p.getIdade(),
                        p.getTipo(),
                        p.getCor(),
                        p.getPeso()
                ))
                .toList();

        return new AbrigoDTO(
                abrigo.getId(),
                abrigo.getNome(),
                abrigo.getTelefone(),
                abrigo.getEmail(),
                pets
        );
    }
}
