package br.com.alura.adopet.api.infra.exceptions;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        boolean sucesso,
        String mensagem,
        T dados,
        LocalDateTime timestamp
) {
    // Factory methods para facilitar a criação
    public static <T> ApiResponse<T> sucesso(String mensagem) {
        return new ApiResponse<>(true, mensagem, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> sucesso(String mensagem, T dados) {
        return new ApiResponse<>(true, mensagem, dados, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> erro(String mensagem) {
        return new ApiResponse<>(false, mensagem, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> erro(String mensagem, T dados) {
        return new ApiResponse<>(false, mensagem, dados, LocalDateTime.now());
    }
}