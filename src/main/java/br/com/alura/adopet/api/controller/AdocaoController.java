package br.com.alura.adopet.api.controller;

import br.com.alura.adopet.api.model.dto.adocaoDTO.ReprovarAdocaoDTO;
import br.com.alura.adopet.api.model.dto.adocaoDTO.SolicitacaoAdocaoDTO;
import br.com.alura.adopet.api.service.AdocaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adocoes")
public class AdocaoController {

    private final AdocaoService adocaoService;

    public AdocaoController(AdocaoService adocaoService) {
        this.adocaoService = adocaoService;
    }

    @PostMapping
    public ResponseEntity<String> solicitar(@RequestBody @Valid SolicitacaoAdocaoDTO dto) {
        adocaoService.solicitar(dto);
        return ResponseEntity.ok("Solicitação de adoção registrada com sucesso!");
    }

    @PutMapping("/aprovar/{id}")
    public ResponseEntity<String> aprovar(@PathVariable Long id) {
        adocaoService.aprovar(id);
        return ResponseEntity.ok("Adoção aprovada com sucesso!");
    }


    @PutMapping("/reprovar/{id}")
    public ResponseEntity<String> reprovar(@PathVariable Long id, @RequestBody @Valid ReprovarAdocaoDTO dto) {
        adocaoService.reprovar(id, dto.justificativa());
        return ResponseEntity.ok("Adoção reprovada com sucesso!");
    }
}
