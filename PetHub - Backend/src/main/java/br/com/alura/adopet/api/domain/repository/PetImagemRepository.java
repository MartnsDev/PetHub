package br.com.alura.adopet.api.domain.repository;


import br.com.alura.adopet.api.domain.model.PetImagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetImagemRepository extends JpaRepository<PetImagem, Long> {

}

