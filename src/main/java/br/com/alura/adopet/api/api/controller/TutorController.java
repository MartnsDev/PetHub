package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.tutorDTO.AtualizarTutorDTO;
import br.com.alura.adopet.api.api.dto.tutorDTO.CadastrarTutorDTO;
import br.com.alura.adopet.api.domain.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tutores")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<String> cadastrar(@RequestBody @Valid CadastrarTutorDTO dto) {
        tutorService.cadastrar(dto);
        return ResponseEntity.ok("Tutor cadastrado com sucesso!");
    }


    @PutMapping
    @Transactional
    public ResponseEntity<String> atualizar(@RequestBody @Valid AtualizarTutorDTO dto) {
        tutorService.atualizar(dto);
        return ResponseEntity.ok("Tutor atualizado com sucesso!");
    }
}
