package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.api.dto.abrigoDTO.AbrigoDTO;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarAbrigoDTO;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarPetDTO;
import br.com.alura.adopet.api.api.dto.petDTO.PetDTO;
import br.com.alura.adopet.api.domain.model.Abrigo;
import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.domain.model.PetImagem;
import br.com.alura.adopet.api.domain.repository.AbrigoRepository;
import br.com.alura.adopet.api.domain.repository.PetRepository;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AbrigoService {

    private final AbrigoRepository abrigoRepository;
    private final PetRepository petRepository;

    public AbrigoService(AbrigoRepository abrigoRepository, PetRepository petRepository) {
        this.abrigoRepository = abrigoRepository;
        this.petRepository = petRepository;
    }

    // ------------------- Abrigos -------------------

    @Transactional(readOnly = true) // Somente leitura, não altera o banco
    public List<AbrigoDTO> listarTodosOsAbrigos() {
        // Converte cada Abrigo para AbrigoDTO
        return abrigoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .toList();
    }

    @Transactional // Transação para operação de escrita
    public void cadastrar(CadastrarAbrigoDTO dto) {
        // Verifica se já existe abrigo com mesmo nome, telefone ou email
        boolean jaCadastrado = abrigoRepository.existsByNomeOrTelefoneOrEmail(
                dto.nome(), dto.telefone(), dto.email()
        );

        if (jaCadastrado) {
            throw new ValidacaoException("Dados já cadastrados em outro abrigo");
        }

        // Cria novo abrigo e salva
        Abrigo abrigo = Abrigo.builder()
                .nome(dto.nome())
                .email(dto.email())
                .telefone(dto.telefone())
                .build();

        abrigoRepository.save(abrigo);
    }

    @Transactional
    public void deletar(Long idAbrigo) {
        // Verifica se o abrigo existe antes de deletar
        if (!abrigoRepository.existsById(idAbrigo)) {
            throw new ValidacaoException("Abrigo não encontrado");
        }
        abrigoRepository.deleteById(idAbrigo);
    }

    @Transactional(readOnly = true)
    public AbrigoDTO getAbrigo(Long idAbrigo) {
        // Busca abrigo por ID ou lança exceção
        Abrigo abrigo = abrigoRepository.findById(idAbrigo)
                .orElseThrow(() -> new ValidacaoException("Abrigo não encontrado"));
        return converterParaDTO(abrigo);
    }

    // ------------------- Pets -------------------

    @Transactional
    public void cadastrarPet(Long idAbrigo, CadastrarPetDTO dto) {
        // Busca o abrigo pelo ID
        Abrigo abrigo = abrigoRepository.findById(idAbrigo)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado"));

        // Cria novo pet
        Pet pet = new Pet();
        pet.setNome(dto.nome());
        pet.setRaca(dto.raca());
        pet.setIdade(dto.idade());
        pet.setTipo(dto.tipo());
        pet.setCor(dto.cor());
        pet.setPeso(dto.peso());
        pet.setAbrigo(abrigo);
        pet.setAdotado(false);
        pet.setImagens(new ArrayList<>()); // garante que não seja null

        petRepository.save(pet); // Salva no banco
    }

    @Transactional(readOnly = true)
    public List<PetDTO> listarPets(String idOuNome) {
        // Busca abrigo pelo ID ou nome
        Abrigo abrigo = buscarAbrigoPorIdOuNome(idOuNome);

        // Filtra pets não adotados e converte para DTO
        return abrigo.getPets().stream()
                .filter(p -> !p.getAdotado())
                .map(this::converterPetParaDTO)
                .toList();
    }

    // ------------------- Auxiliares -------------------

    private Abrigo buscarAbrigoPorIdOuNome(String idOuNome) {
        try {
            // Tenta converter para ID (Long)
            Long id = Long.parseLong(idOuNome);
            return abrigoRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado"));
        } catch (NumberFormatException e) {
            // Se não for número, busca pelo nome
            return abrigoRepository.findByNome(idOuNome)
                    .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado"));
        }
    }

    private AbrigoDTO converterParaDTO(Abrigo abrigo) {
        // Converte pets do abrigo para DTO, apenas os não adotados
        List<PetDTO> pets = abrigo.getPets().stream()
                .filter(p -> !p.getAdotado())
                .map(this::converterPetParaDTO)
                .toList();

        // Cria AbrigoDTO
        return new AbrigoDTO(
                abrigo.getId(),
                abrigo.getNome(),
                abrigo.getTelefone(),
                abrigo.getEmail(),
                pets
        );
    }

    private PetDTO converterPetParaDTO(Pet pet) {
        // Converte lista de imagens para URLs, garantindo não null
        List<String> fotos = pet.getImagens() != null
                ? pet.getImagens().stream().map(PetImagem::getUrl).toList()
                : List.of();

        // Pega email do abrigo, se existir
        String emailAbrigo = pet.getAbrigo() != null ? pet.getAbrigo().getEmail() : "";

        // Retorna DTO do pet
        return new PetDTO(
                pet.getId(),
                pet.getNome(),
                pet.getRaca(),
                pet.getIdade(),
                pet.getCor(),
                pet.getPeso(),
                pet.getTipo().name(),
                pet.getAdotado(),
                fotos,
                emailAbrigo
        );
    }
}
