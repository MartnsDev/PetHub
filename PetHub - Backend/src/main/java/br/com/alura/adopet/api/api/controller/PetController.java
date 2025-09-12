    package br.com.alura.adopet.api.api.controller;

    import br.com.alura.adopet.api.domain.model.Pet;
    import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarPetDTO;
    import br.com.alura.adopet.api.domain.service.PetService;
    import jakarta.validation.Valid;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.net.URI;
    import java.util.List;

    @RestController
    @RequestMapping("/pets")
    public class PetController {

        private final PetService petService;

        public PetController(PetService petService) {
            this.petService = petService;
        }

        // GET /pets/disponiveis -> Retorna todos os pets que não foram adotados
        @GetMapping("/disponiveis")
        public ResponseEntity<List<Pet>> listarTodosDisponiveis() {
            List<Pet> disponiveis = petService.listarTodosDisponiveis();
            return ResponseEntity.ok(disponiveis);
        }


        // POST /abrigos/{id}/pets -> cadastra pet no abrigo
        @PostMapping("/{id}/pets")
        public ResponseEntity<Void> cadastrarPet(@PathVariable Long idAbrigo, @RequestBody @Valid CadastrarPetDTO dto) {
            petService.cadastrarPet(idAbrigo, dto);
            return ResponseEntity.created(URI.create("/abrigos/" + idAbrigo + "/pets")).build();
        }

    }
