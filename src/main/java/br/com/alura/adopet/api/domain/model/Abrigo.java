package br.com.alura.adopet.api.domain.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "abrigos")
@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Abrigo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String telefone;
    private String email;

    @OneToMany(mappedBy = "abrigo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("abrigo_pets")
    private List<Pet> pets;


}


