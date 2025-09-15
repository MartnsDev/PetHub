package br.com.alura.adopet.api.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pet_images") // Nome da tabela no banco
@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
@Builder // Padrão Builder para criação de objetos
public class PetImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador único da imagem

    private String url; // URL da imagem armazenada (pode ser local ou em um bucket na nuvem)

    @ManyToOne(fetch = FetchType.EAGER) // Muitas imagens podem estar ligadas a um Pet
    @JoinColumn(name = "pet_id") // Cria a coluna de chave estrangeira "pet_id"
    @JsonBackReference // Evita loop infinito na serialização JSON (lado "filho" da relação)
    private Pet pet; // Referência ao pet dono dessa imagem
}
