package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.api.dto.petDTO.PetDTO;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarPetDTO;
import br.com.alura.adopet.api.domain.model.Abrigo;
import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.domain.model.PetImagem;
import br.com.alura.adopet.api.domain.repository.AbrigoRepository;
import br.com.alura.adopet.api.domain.repository.PetImagemRepository;
import br.com.alura.adopet.api.domain.repository.PetRepository;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final AbrigoRepository abrigoRepository;
    private final PetImagemRepository petImagemRepository;

    public PetService(PetRepository petRepository,
                      AbrigoRepository abrigoRepository,
                      PetImagemRepository petImagemRepository) {
        this.petRepository = petRepository;
        this.abrigoRepository = abrigoRepository;
        this.petImagemRepository = petImagemRepository;
    }

    // ------------------- Listagem -------------------

    /** Retorna todos os pets disponíveis */
    @Transactional(readOnly = true)
    public List<Pet> listarTodosDisponiveis() {
        return petRepository.findByAdotadoFalse();
    }

    /** Retorna todos os pets disponíveis como DTOs para o frontend */
    @Transactional(readOnly = true)
    public List<PetDTO> listarTodosDisponiveisDTO() {
        return petRepository.findByAdotadoFalse().stream()
                .map(this::converterParaDTO)
                .toList();
    }

    /** Converte Pet para PetDTO */
    private PetDTO converterParaDTO(Pet pet) {
        List<String> imagens = pet.getImagens().stream()
                .map(PetImagem::getUrl)
                .toList();

        String emailAbrigo = pet.getAbrigo() != null ? pet.getAbrigo().getEmail() : "";

        return new PetDTO(
                pet.getId(),
                pet.getNome(),
                pet.getRaca(),
                pet.getIdade(),
                pet.getCor(),
                pet.getPeso(),
                pet.getTipo().name(),
                pet.getAdotado(),
                imagens,
                emailAbrigo
        );
    }

    // ------------------- Cadastro -------------------

    /** Cadastra um novo pet sem imagens */
    @Transactional
    public Pet cadastrarPet(Long idAbrigo, CadastrarPetDTO dto) {
        Abrigo abrigo = abrigoRepository.findById(idAbrigo)
                .orElseThrow(() -> new EntityNotFoundException("Abrigo não encontrado"));

        Pet pet = Pet.builder()
                .nome(dto.nome())
                .raca(dto.raca())
                .idade(dto.idade())
                .tipo(dto.tipo())
                .cor(dto.cor())
                .peso(dto.peso())
                .adotado(false)
                .abrigo(abrigo)
                .build();

        return petRepository.save(pet);
    }

    /** Salva imagens de um pet, criando diretórios se necessário */
    @Transactional
    public void salvarImagens(Long petId, List<MultipartFile> fotos) {
        Pet pet = petRepository.findByIdWithImages(petId)
                .orElseThrow(() -> new ValidacaoException("Pet não encontrado"));


        if (pet.getImagens() == null) {
            pet.setImagens(new ArrayList<>());
        }

        for (MultipartFile foto : fotos) {
            if (foto.isEmpty()) continue;

            try {
                String uploadDir = new File("uploads/pets/" + petId).getAbsolutePath();
                File dir = new File(uploadDir);
                if (!dir.exists() && !dir.mkdirs()) {
                    throw new RuntimeException("Não foi possível criar diretório: " + uploadDir);
                }

                String safeName = foto.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                String fileName = UUID.randomUUID() + "_" + safeName;
                File destino = new File(dir, fileName);
                foto.transferTo(destino);

                PetImagem imagem = new PetImagem();
                imagem.setUrl("/uploads/pets/" + petId + "/" + fileName);
                imagem.setPet(pet);
                petImagemRepository.save(imagem);
                pet.getImagens().add(imagem);

            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar imagem: " + foto.getOriginalFilename(), e);
            }
        }

        petRepository.save(pet);
    }


    // ------------------- Auxiliares -------------------

    /** Converte Pet para PetDTO (público) */
    @Transactional
    public PetDTO converterPetParaDTO(Pet pet) {
        if (pet.getImagens() != null) {
            pet.getImagens().size(); // força carregamento das imagens
        }

        List<String> imagens = pet.getImagens() != null
                ? pet.getImagens().stream().map(PetImagem::getUrl).toList()
                : List.of();

        String emailAbrigo = pet.getAbrigo() != null ? pet.getAbrigo().getEmail() : "";

        return new PetDTO(
                pet.getId(),
                pet.getNome(),
                pet.getRaca(),
                pet.getIdade(),
                pet.getCor(),
                pet.getPeso(),
                pet.getTipo().name(),
                pet.getAdotado(),
                imagens,
                emailAbrigo
        );
    }


    /** Busca pet por ID */
    @Transactional(readOnly = true)
    public Pet buscarPorIdComImagens(Long id) {
        return petRepository.findByIdWithImages(id)
                .orElseThrow(() -> new ValidacaoException("Pet não encontrado"));
    }

}
