package br.com.alura.adopet.api.infra.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Classe responsável por capturar exceções globais da aplicação
 * e retornar uma resposta padronizada (ApiResponse).
 *
 * Centraliza o tratamento de erros para evitar duplicação
 * e melhorar a experiência do cliente da API.
 */
@RestControllerAdvice
public class ExceptionHandlerController {

    // ====================================
    // 1) Exceções personalizadas
    // ====================================

    /**
     * Trata erros de validação de regras de negócio (ValidacaoException).
     * Exemplo: email já cadastrado, tutor não encontrado, etc.
     */
    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<ApiResponse> handleValidacaoException(ValidacaoException ex) {
        ApiResponse response = new ApiResponse(
                false,
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(response);
    }

    // ====================================
    // 2) Runtime genéricas
    // ====================================

    /**
     * Captura RuntimeException genéricas para evitar stacktrace
     * sendo exposto diretamente ao cliente.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
        ApiResponse response = new ApiResponse(
                false,
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(response);
    }

    // ====================================
    // 3) Erros de desserialização (JSON → DTO)
    // ====================================

    /**
     * Trata erros ao converter JSON para DTOs.
     * Exemplo: campo enum com valor inválido.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleEnumDeserialization(HttpMessageNotReadableException ex) {
        String mensagem = "Erro de formato: valor inválido para algum campo";

        // Caso o erro seja em um Enum, detalhamos quais valores são aceitos
        if (ex.getCause() instanceof InvalidFormatException invalidEx) {
            String campo = invalidEx.getPath().get(0).getFieldName();
            Class<?> tipo = invalidEx.getTargetType();

            if (tipo.isEnum()) {
                mensagem = "Campo '" + campo + "' inválido. Valores aceitos: "
                        + String.join(", ",
                        java.util.Arrays.stream(tipo.getEnumConstants())
                                .map(Object::toString)
                                .toList());
            }
        }

        ApiResponse response = new ApiResponse(false, mensagem, null, LocalDateTime.now());
        return ResponseEntity.badRequest().body(response);
    }

    // ====================================
    // 4) Erros de validação (Bean Validation @Valid)
    // ====================================

    /**
     * Captura erros de validação de DTOs (ex: @NotBlank, @Email).
     * Retorna a primeira mensagem de erro encontrada.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
        // Extrai a primeira mensagem de erro do DTO
        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Erro de validação");

        ApiResponse response = new ApiResponse(
                false,
                mensagem,
                null,
                LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(response);
    }
}
