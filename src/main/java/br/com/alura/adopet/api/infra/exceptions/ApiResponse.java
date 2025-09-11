package br.com.alura.adopet.api.infra.exceptions;

import java.time.LocalDateTime;

public record ApiResponse(
        boolean sucesso,
        String mensagem,
        Object dados,
        LocalDateTime timestamp
) {}
