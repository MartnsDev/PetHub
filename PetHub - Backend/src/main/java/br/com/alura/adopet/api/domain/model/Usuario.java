package br.com.alura.adopet.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios") // Nome da tabela
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String nome;

    // Se quiser adicionar roles futuramente:
    // @Enumerated(EnumType.STRING)
    // private Role role;
}
