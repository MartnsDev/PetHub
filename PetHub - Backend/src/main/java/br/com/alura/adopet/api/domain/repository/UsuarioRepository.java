package br.com.alura.adopet.api.domain.repository;

import br.com.alura.adopet.api.domain.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(@Email(message = "Email inválido") @NotBlank(message = "Email é obrigatório") String email);
}
