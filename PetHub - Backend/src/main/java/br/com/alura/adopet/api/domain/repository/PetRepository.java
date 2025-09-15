package br.com.alura.adopet.api.domain.repository;

import br.com.alura.adopet.api.domain.model.Pet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    // Força carregar imagens e abrigo junto com o pet
    @EntityGraph(attributePaths = {"imagens", "abrigo"})
    List<Pet> findByAdotadoFalse();

    @Query("SELECT p.adotado FROM Pet p WHERE p.id = :id")
    Boolean isAdotado(@Param("id") Long id);

    @Query("SELECT p FROM Pet p LEFT JOIN FETCH p.imagens WHERE p.id = :id")
    Optional<Pet> findByIdWithImages(@Param("id") Long id);

}
