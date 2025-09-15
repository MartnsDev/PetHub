package br.com.alura.adopet.api.api.controller;

import br.com.alura.adopet.api.api.dto.recuperarSenhaDTO.AtualizarSenhaRequestDTO;
import br.com.alura.adopet.api.api.dto.recuperarSenhaDTO.SolicitarCodigoRequestDTO;
import br.com.alura.adopet.api.api.dto.recuperarSenhaDTO.VerificarCodigoRequestDTO;
import br.com.alura.adopet.api.domain.service.RecuperacaoSenhaService;
import br.com.alura.adopet.api.infra.exceptions.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Define a rota base para autenticação e recuperação de senha
@Slf4j // Habilita logging com Lombok
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService recuperacaoService;

    public RecuperacaoSenhaController(RecuperacaoSenhaService recuperacaoService) {
        this.recuperacaoService = recuperacaoService;
    }

    // POST /api/auth/solicitar-codigo -> solicita envio de código para recuperar senha
    @PostMapping("/solicitar-codigo")
    public ResponseEntity<ApiResponse<Void>> solicitarCodigo(
            @Valid @RequestBody SolicitarCodigoRequestDTO request) { // DTO contém o email do usuário
        try {
            recuperacaoService.solicitarCodigoRecuperacao(request); // Chama service para gerar código

            return ResponseEntity.ok(
                    ApiResponse.sucesso("Se o email existir, um código foi enviado para sua caixa de entrada")
            );

        } catch (Exception e) {
            log.error("Erro ao solicitar código de recuperação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.erro("Erro interno do servidor. Tente novamente mais tarde.")
            );
        }
    }

    // POST /api/auth/verificar-codigo -> verifica se o código recebido é válido
    @PostMapping("/verificar-codigo")
    public ResponseEntity<ApiResponse<Void>> verificarCodigo(
            @Valid @RequestBody VerificarCodigoRequestDTO request) { // DTO contém email e código
        try {
            boolean codigoValido = recuperacaoService.verificarCodigo(request); // Verifica código no service

            if (codigoValido) {
                return ResponseEntity.ok(
                        ApiResponse.sucesso("Código verificado com sucesso")
                );
            } else {
                return ResponseEntity.badRequest().body(
                        ApiResponse.erro("Código inválido ou expirado")
                );
            }

        } catch (Exception e) {
            log.error("Erro ao verificar código: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.erro("Erro interno do servidor. Tente novamente mais tarde.")
            );
        }
    }

    // PUT /api/auth/recuperar -> atualiza a senha do usuário
    @PutMapping("/recuperar")
    public ResponseEntity<ApiResponse<Void>> atualizarSenha(
            @Valid @RequestBody AtualizarSenhaRequestDTO request) { // DTO contém email, código e nova senha
        try {
            atualizarSenhaDoEmail(request); // Chama método para atualizar senha no service

            return ResponseEntity.ok(
                    ApiResponse.sucesso("Senha atualizada com sucesso")
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.erro(e.getMessage()) // Caso o service lance erro de validação
            );
        } catch (Exception e) {
            log.error("Erro ao atualizar senha: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.erro("Erro interno do servidor. Tente novamente mais tarde.")
            );
        }
    }

    @Transactional // Garante que a operação de atualização de senha seja transacional
    public void atualizarSenhaDoEmail(AtualizarSenhaRequestDTO request) {
        recuperacaoService.atualizarSenhaDoEmail(request); // Chama service para atualizar senha no banco
    }
}
