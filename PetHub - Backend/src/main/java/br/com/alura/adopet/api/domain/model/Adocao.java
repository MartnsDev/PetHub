package br.com.alura.adopet.api.domain.model;

import br.com.alura.adopet.api.domain.model.enums.StatusAdocao;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "adocoes")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Adocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime data;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference("tutor_adocoes")
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    @JsonManagedReference("adocao_pets")
    private Pet pet;

    private String motivo;

    @Enumerated(EnumType.STRING)
    private StatusAdocao status;

    private String justificativaStatus;


    // Construtor
    public Adocao(Tutor tutor, Pet pet, String motivo) {
        this.tutor = tutor;
        this.pet = pet;
        this.motivo = motivo;
        this.status = StatusAdocao.AGUARDANDO_AVALIACAO;
        this.data = LocalDateTime.now();
    }

    //Contrutor padrao que o JPA Exige
    public Adocao() {
    }

    public void marcarComoAprovada(String justificativa) {
        this.status = StatusAdocao.APROVADO;
        this.justificativaStatus = justificativa;
    }

    public void marcarComoReprovada(String justificativa) {
        this.status = StatusAdocao.REPROVADO;
        this.justificativaStatus = justificativa;
    }
}
