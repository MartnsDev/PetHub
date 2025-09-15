package br.com.alura.adopet.api.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "codigos_recuperacao") // Define a tabela no banco
@Data // Lombok -> gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor vazio (necessário para JPA)
@AllArgsConstructor // Construtor com todos os campos
public class CodigoRecuperacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID auto-incremento
    private Long id;

    @Column(nullable = false) // Email do usuário que solicitou o código
    private String email;

    @Column(nullable = false) // Código gerado e enviado por email
    private String codigo;

    @Column(name = "data_expiracao", nullable = false) // Data limite de validade do código
    private LocalDateTime dataExpiracao;

    @Column(name = "utilizado", nullable = false) // Se o código já foi usado (true/false)
    private Boolean utilizado = false;

    @Column(name = "created_at") // Momento da criação do registro
    private LocalDateTime createdAt;

    @PrePersist // Executa automaticamente antes de salvar no banco
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // Seta data de criação
        this.dataExpiracao = LocalDateTime.now().plusMinutes(15);
        // Código válido por 15 minutos
    }

    // Metodo auxiliar -> verifica se o código já expirou
    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(this.dataExpiracao);
    }
}
