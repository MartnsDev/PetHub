package br.com.alura.adopet.api.domain.repository;

import br.com.alura.adopet.api.domain.model.CodigoRecuperacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CodigoRecuperacaoRepository extends JpaRepository<CodigoRecuperacao, Long> {

    Optional<CodigoRecuperacao> findByEmailAndCodigoAndUtilizadoFalse(String email, String codigo);

    @Modifying
    @Query("DELETE FROM CodigoRecuperacao c WHERE c.dataExpiracao < :agora")
    void deleteByDataExpiracaoLessThan(LocalDateTime agora);

    @Modifying
    @Query("UPDATE CodigoRecuperacao c SET c.utilizado = true WHERE c.email = :email AND c.utilizado = false")
    void marcarComoUtilizadosPorEmail(String email);
}