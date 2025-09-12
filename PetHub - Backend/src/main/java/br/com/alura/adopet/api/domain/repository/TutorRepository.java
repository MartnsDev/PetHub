package br.com.alura.adopet.api.domain.repository;

import br.com.alura.adopet.api.domain.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    boolean existsByEmailOrTelefone(String email, String telefone);
}
