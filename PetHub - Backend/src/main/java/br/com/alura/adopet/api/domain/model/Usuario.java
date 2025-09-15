package br.com.alura.adopet.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios") // Define o nome da tabela no banco
@Getter // Lombok: gera automaticamente os getters
@Setter // Lombok: gera automaticamente os setters
@NoArgsConstructor // Construtor sem argumentos (necessário para JPA)
@AllArgsConstructor // Construtor com todos os argumentos
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único do usuário (PK)

    @Column(nullable = false, unique = true)
    private String email; // E-mail do usuário (não pode ser nulo e deve ser único)

    @Column(nullable = false)
    private String senha; // Senha do usuário (armazenada criptografada)

    @Column(nullable = false)
    private String nome; // Nome completo do usuário

    // Setter customizado para senha
    // -> usado quando você já tem a senha criptografada (ex: BCrypt)
    public void setSenha(String encode) {
        this.senha = encode;
    }
}
