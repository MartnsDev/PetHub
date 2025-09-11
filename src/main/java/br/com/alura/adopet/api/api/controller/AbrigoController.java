package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.abrigoDTO.AbrigoDTO;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarAbrigoDTO;
import br.com.alura.adopet.api.api.dto.petDTO.PetDTO;
import br.com.alura.adopet.api.domain.service.AbrigoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/abrigos")
public class AbrigoController {

    private final AbrigoService abrigoService;

    public AbrigoController(AbrigoService abrigoService) {
        this.abrigoService = abrigoService;
    }

    // GET /abrigos -> lista todos
    @GetMapping
    public ResponseEntity<List<AbrigoDTO>> listarTodosOsAbrigosComOsPets() {
        return ResponseEntity.ok(abrigoService.listarTodosOsAbrigos());
    }

    // POST /abrigos -> cadastra novo
    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestBody @Valid CadastrarAbrigoDTO dto) {
        abrigoService.cadastrar(dto);
        return ResponseEntity.created(URI.create("/abrigos")).build();
    }

    // GET /abrigos/{id} -> busca abrigo com pets
    @GetMapping("/{id}")
    public ResponseEntity<AbrigoDTO> buscarAbrigosComPets(@PathVariable Long idAbrigo) {
        return ResponseEntity.ok(abrigoService.getAbrigo(idAbrigo));
    }

    // GET /abrigos/{idOuNome}/pets -> lista pets do abrigo
    @GetMapping("/{idOuNome}/pets")
    public ResponseEntity<List<PetDTO>> listarPetsDeUmAbrigo(@PathVariable String idOuNome) {
        return ResponseEntity.ok(abrigoService.listarPets(idOuNome));
    }

    // DELETE /abrigos/{id} -> deleta abrigo
    @DeleteMapping("/{idAbrigo}")
    public ResponseEntity<Void> deletar(@PathVariable Long idAbrigo) {
        abrigoService.deletar(idAbrigo);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
