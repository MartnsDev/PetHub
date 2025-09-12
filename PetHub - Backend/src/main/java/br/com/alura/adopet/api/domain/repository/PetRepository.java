package br.com.alura.adopet.api.domain.repository;

import br.com.alura.adopet.api.domain.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByAdotadoFalse();

    @Query("SELECT p.adotado FROM Pet p WHERE p.id = :id")
    Boolean isAdotado(@Param("id") Long id);

}
