package br.com.alura.adopet.api.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity // Indica que esta classe é uma entidade JPA (será mapeada no banco)
@Table(name = "abrigos") // Nome da tabela no banco de dados
@Data // Lombok: gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
@Builder // Padrão Builder para instanciar objetos de forma fluente
public class Abrigo {

    @Id // Chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Geração automática do ID (autoincrement no banco)
    private Long id;

    // Dados básicos do abrigo
    private String nome;
    private String telefone;
    private String email;

    @OneToMany(
            mappedBy = "abrigo", // Relacionamento é mapeado pelo atributo "abrigo" na classe Pet
            cascade = CascadeType.ALL, // Se deletar/atualizar um Abrigo, afeta os Pets associados
            fetch = FetchType.LAZY // Carregamento preguiçoso (só busca pets quando necessário)
    )
    @JsonManagedReference("abrigo_pets")
    // Ajuda o Jackson a evitar loop infinito na serialização (lado "pai" da relação)
    private List<Pet> pets; // Lista de pets vinculados a este abrigo
}
