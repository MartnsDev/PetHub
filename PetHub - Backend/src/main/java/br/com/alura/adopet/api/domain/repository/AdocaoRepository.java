package br.com.alura.adopet.api.domain.repository;

import br.com.alura.adopet.api.domain.model.Adocao;
import br.com.alura.adopet.api.domain.model.enums.StatusAdocao;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdocaoRepository extends JpaRepository<Adocao, Long> {

    boolean existsByTutorIdAndStatus(@NotNull Long idTutor, StatusAdocao statusAdocao);

    boolean existsByPetIdAndStatus(@NotNull Long idPet, StatusAdocao statusAdocao);

    long countByTutorIdAndStatus(@NotNull Long idTutor, StatusAdocao statusAdocao);

}
