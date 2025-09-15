package br.com.alura.adopet.api.domain.model;

import br.com.alura.adopet.api.domain.model.enums.StatusAdocao;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "adocoes") // Define a tabela "adocoes" no banco
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Adocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Chave primária auto-incremento
    private Long id;

    private LocalDateTime data; // Data da solicitação de adoção

    @ManyToOne(fetch = FetchType.LAZY) // Muitas adoções podem estar vinculadas a 1 tutor
    @JsonBackReference("tutor_adocoes") // Evita loop de serialização no lado do Tutor
    @JoinColumn(name = "tutor_id") // Define a coluna de chave estrangeira
    private Tutor tutor;

    @OneToOne(fetch = FetchType.LAZY) // Cada adoção está ligada a 1 pet (e vice-versa)
    @JoinColumn(name = "pet_id") // Coluna que armazena o ID do pet
    @JsonManagedReference("adocao_pets") // Lado "pai" da relação com Pet
    private Pet pet;

    private String motivo; // Motivo pelo qual o tutor deseja adotar o pet

    @Enumerated(EnumType.STRING) // Salva o ENUM como texto no banco (mais legível)
    private StatusAdocao status; // Status da adoção (aguardando, aprovado, reprovado)

    private String justificativaStatus; // Explicação caso seja aprovada/reprovada

    // Construtor usado quando criamos uma nova adoção
    public Adocao(Tutor tutor, Pet pet, String motivo) {
        this.tutor = tutor;
        this.pet = pet;
        this.motivo = motivo;
        this.status = StatusAdocao.AGUARDANDO_AVALIACAO; // Status inicial
        this.data = LocalDateTime.now(); // Data atual
    }

    // Construtor padrão exigido pelo JPA
    public Adocao() {
    }

    // Marca a adoção como aprovada e adiciona justificativa
    public void marcarComoAprovada(String justificativa) {
        this.status = StatusAdocao.APROVADO;
        this.justificativaStatus = justificativa;
    }

    // Marca a adoção como reprovada e adiciona justificativa
    public void marcarComoReprovada(String justificativa) {
        this.status = StatusAdocao.REPROVADO;
        this.justificativaStatus = justificativa;
    }
}
