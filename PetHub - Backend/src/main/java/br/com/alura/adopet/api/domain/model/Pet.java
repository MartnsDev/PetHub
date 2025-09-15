package br.com.alura.adopet.api.domain.model;

import br.com.alura.adopet.api.domain.model.enums.TipoPet;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity // Define que esta classe é uma entidade JPA
@Table(name = "pets") // Mapeia para a tabela "pets"
@Data // Lombok -> gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Construtor vazio (necessário para o JPA)
@AllArgsConstructor // Construtor com todos os campos
@Builder // Padrão Builder para instanciar objetos
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID auto-incremento
    private Long id;

    @Enumerated(EnumType.STRING) // Salva o enum como String no banco (ex: "CACHORRO")
    private TipoPet tipo;

    private String nome;
    private String raca;
    private Integer idade;
    private String cor;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    // Relacionamento 1:N com PetImagem
    // cascade = ALL -> operações em Pet afetam as imagens
    // orphanRemoval = true -> remove imagens "órfãs" quando retiradas da lista
    private List<PetImagem> imagens = new ArrayList<>();

    @Column(precision = 5, scale = 2) // Exemplo: 10.25 kg (5 dígitos no total, 2 após vírgula)
    private BigDecimal peso;

    private Boolean adotado = false; // Marca se o pet já foi adotado

    @ManyToOne // Muitos pets podem estar em 1 abrigo
    @JsonBackReference("abrigo_pets") // Evita loop infinito na serialização JSON
    @JoinColumn(name = "abrigo_id") // Chave estrangeira para Abrigo
    private Abrigo abrigo;
}
