package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.petDTO.PetDTO;
import br.com.alura.adopet.api.domain.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pets") // Define a rota base para Pets
public class PetController {

    private final PetService petService; // Serviço que manipula dados de pets

    public PetController(PetService petService) {
        this.petService = petService;
    }

    // GET /pets/disponiveis -> Lista todos os pets que ainda não foram adotados
    @GetMapping("/disponiveis")
    public ResponseEntity<List<PetDTO>> listarTodosDisponiveis() {
        // Busca lista de pets disponíveis via service
        List<PetDTO> disponiveis = petService.listarTodosDisponiveisDTO();
        return ResponseEntity.ok(disponiveis); // Retorna 200 OK com lista
    }

    // POST /pets/{id}/imagens -> Faz upload de imagens de um pet específico
    @PostMapping("/{id}/imagens")
    public ResponseEntity<Void> uploadImagens(
            @PathVariable Long id,                  // ID do pet
            @RequestParam("fotos") List<MultipartFile> fotos // Lista de arquivos enviados
    ) {
        petService.salvarImagens(id, fotos); // Salva as imagens no serviço
        return ResponseEntity.ok().build();  // Retorna 200 OK sem corpo
    }
}
