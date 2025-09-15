package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.abrigoDTO.AbrigoDTO;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarAbrigoDTO;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarPetDTO;
import br.com.alura.adopet.api.api.dto.petDTO.PetDTO;
import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.domain.service.AbrigoService;
import br.com.alura.adopet.api.domain.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/abrigos")
public class AbrigoController {

    private final AbrigoService abrigoService;
    private final PetService petService;

    public AbrigoController(AbrigoService abrigoService,
                            PetService petService) {
        this.abrigoService = abrigoService;
        this.petService = petService;
    }

    // ------------------- ABRIGOS -------------------

    @GetMapping
    public ResponseEntity<List<AbrigoDTO>> listarTodosOsAbrigosComOsPets() {
        return ResponseEntity.ok(abrigoService.listarTodosOsAbrigos());
    }

    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestBody @Valid CadastrarAbrigoDTO dto) {
        abrigoService.cadastrar(dto);
        return ResponseEntity.created(URI.create("/abrigos")).build();
    }

    @GetMapping("/{idAbrigo}")
    public ResponseEntity<AbrigoDTO> buscarAbrigosComPets(@PathVariable Long idAbrigo) {
        return ResponseEntity.ok(abrigoService.getAbrigo(idAbrigo));
    }

    @DeleteMapping("/{idAbrigo}")
    public ResponseEntity<Void> deletar(@PathVariable Long idAbrigo) {
        abrigoService.deletar(idAbrigo);
        return ResponseEntity.noContent().build();
    }

    // ------------------- PETS -------------------

    // POST /abrigos/{idAbrigo}/pets -> cadastra pet com imagens
    @PostMapping("/{idAbrigo}/pets")
    public ResponseEntity<PetDTO> cadastrarPet(
            @PathVariable Long idAbrigo,
            @ModelAttribute @Valid CadastrarPetDTO dto,
            @RequestParam(value = "fotos", required = false) List<MultipartFile> fotos
    ) {
        // 1️⃣ Cadastra o pet (sem imagens)
        Pet pet = petService.cadastrarPet(idAbrigo, dto);

        // 2️⃣ Salva imagens (se existirem)
        if (fotos != null && !fotos.isEmpty()) {
            List<MultipartFile> fotosValidas = fotos.stream()
                    .filter(f -> !f.isEmpty())
                    .toList();

            if (!fotosValidas.isEmpty()) {
                petService.salvarImagens(pet.getId(), fotosValidas);
                // 🔹 Usa o método que busca pet com imagens carregadas
                pet = petService.buscarPorIdComImagens(pet.getId());
            }
        }

        // 3️⃣ Converte pet para DTO e retorna
        PetDTO petDTO = petService.converterPetParaDTO(pet);
        return ResponseEntity.status(201).body(petDTO);
    }

    @GetMapping("/{idOuNome}/pets")
    public ResponseEntity<List<PetDTO>> listarPetsDeUmAbrigo(@PathVariable String idOuNome) {
        return ResponseEntity.ok(abrigoService.listarPets(idOuNome));
    }
}
