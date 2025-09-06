package br.com.alura.adopet.api.controller;

import br.com.alura.adopet.api.model.Pet;
import br.com.alura.adopet.api.service.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<Pet>> listarTodosDisponiveis() {
        List<Pet> disponiveis = petService.listarTodosDisponiveis();
        return ResponseEntity.ok(disponiveis);
    }
}
