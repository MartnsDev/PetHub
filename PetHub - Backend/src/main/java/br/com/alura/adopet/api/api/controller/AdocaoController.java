package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.adocaoDTO.ReprovarAdocaoDTO;
import br.com.alura.adopet.api.api.dto.adocaoDTO.SolicitacaoAdocaoDTO;
import br.com.alura.adopet.api.domain.service.AdocaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adocoes") // Define a rota base para todas as operações relacionadas a adoções
public class AdocaoController {

    private final AdocaoService adocaoService;

    public AdocaoController(AdocaoService adocaoService) {
        this.adocaoService = adocaoService;
    }

    /**
     * Endpoint para criar uma nova solicitação de adoção.
     * Recebe no corpo da requisição o ID do pet, ID do tutor e a justificativa.
     */
    @PostMapping
    public ResponseEntity<String> solicitar(@RequestBody @Valid SolicitacaoAdocaoDTO dto) {
        adocaoService.solicitar(dto); // Chama o serviço que processa a solicitação de adoção
        return ResponseEntity.ok("Solicitação de adoção registrada com sucesso!");
    }

    /**
     * Endpoint para aprovar uma solicitação de adoção.
     * Recebe o ID da solicitação e aprova a adoção correspondente.
     */
    @PutMapping("/aprovar/{id}")
    public ResponseEntity<String> aprovar(@PathVariable Long id) {
        adocaoService.aprovar(id); // Chama o serviço que aprova a adoção pelo ID
        return ResponseEntity.ok("Adoção aprovada com sucesso!");
    }

    /**
     * Endpoint para reprovar uma solicitação de adoção.
     * Recebe o ID da solicitação e a justificativa para a reprovação no corpo da requisição.
     */
    @PutMapping("/reprovar/{id}")
    public ResponseEntity<String> reprovar(@PathVariable Long id,
                                           @RequestBody @Valid ReprovarAdocaoDTO dto) {
        adocaoService.reprovar(id, dto.justificativa()); // Chama o serviço que reprova a adoção com justificativa
        return ResponseEntity.ok("Adoção reprovada com sucesso!");
    }
}
