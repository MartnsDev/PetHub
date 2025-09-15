package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.tutorDTO.AtualizarTutorDTO;
import br.com.alura.adopet.api.api.dto.tutorDTO.CadastrarTutorDTO;
import br.com.alura.adopet.api.domain.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tutores") // Define a rota base para operações de tutor
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    // POST /tutores -> cadastra um novo tutor
    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody @Valid CadastrarTutorDTO dto) {
        tutorService.cadastrar(dto); // Chama o service para cadastrar o tutor
        return ResponseEntity.ok("Tutor cadastrado com sucesso!"); // Retorna 200 OK
    }

    // PUT /tutores -> atualiza um tutor existente
    @PutMapping
    public ResponseEntity<String> atualizar(@RequestBody @Valid AtualizarTutorDTO dto) {
        tutorService.atualizar(dto); // Chama o service para atualizar o tutor
        return ResponseEntity.ok("Tutor atualizado com sucesso!"); // Retorna 200 OK
    }
}
