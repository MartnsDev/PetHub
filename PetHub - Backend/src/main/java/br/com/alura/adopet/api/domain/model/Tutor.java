package br.com.alura.adopet.api.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tutores") // Define o nome da tabela no banco
@Data // Lombok: gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
@Builder // Permite criar objetos com o padrão Builder
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único do tutor (PK)

    private String nome;     // Nome do tutor
    private String telefone; // Telefone de contato
    private String email;    // E-mail do tutor

    @OneToMany(mappedBy = "tutor")
    @JsonManagedReference("tutor_adocoes")
    private List<Adocao> adocoes;
    // Um tutor pode ter várias adoções
    // mappedBy = "tutor" indica que o lado dono da relação está em Adocao
    // @JsonManagedReference ajuda a evitar loop infinito na serialização
}
