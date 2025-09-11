package br.com.alura.adopet.api.domain.repository;

import br.com.alura.adopet.api.domain.model.Tutor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    boolean existsByEmailOrTelefone(String email, String telefone);
}
