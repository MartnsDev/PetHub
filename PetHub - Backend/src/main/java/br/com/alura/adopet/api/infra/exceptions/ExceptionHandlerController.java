package br.com.alura.adopet.api.infra.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionHandlerController {

    // Para nossas exceções personalizadas
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

    // Para runtime genéricas
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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleEnumDeserialization(HttpMessageNotReadableException ex) {
        String mensagem = "Erro de formato: valor inválido para algum campo";

        // Tenta identificar se é erro de enum
        if (ex.getCause() instanceof InvalidFormatException invalidEx) {
            String campo = invalidEx.getPath().get(0).getFieldName();
            Class<?> tipo = invalidEx.getTargetType();
            if (tipo.isEnum()) {
                mensagem = "Campo '" + campo + "' inválido. Valores aceitos: " + String.join(", ",
                        java.util.Arrays.stream(tipo.getEnumConstants())
                                .map(Object::toString)
                                .toList());
            }
        }

        ApiResponse response = new ApiResponse(false, mensagem, null, LocalDateTime.now());
        return ResponseEntity.badRequest().body(response);
    }

    // Para erros de validação do DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
        // Pega a primeira mensagem de erro do DTO
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
