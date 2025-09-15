package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.api.dto.recuperarSenhaDTO.AtualizarSenhaRequestDTO;
import br.com.alura.adopet.api.api.dto.recuperarSenhaDTO.SolicitarCodigoRequestDTO;
import br.com.alura.adopet.api.api.dto.recuperarSenhaDTO.VerificarCodigoRequestDTO;
import br.com.alura.adopet.api.domain.model.CodigoRecuperacao;
import br.com.alura.adopet.api.domain.model.Usuario;
import br.com.alura.adopet.api.domain.repository.CodigoRecuperacaoRepository;
import br.com.alura.adopet.api.domain.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class RecuperacaoSenhaService {

    private final UsuarioRepository usuarioRepository; // Ajuste conforme sua entidade de usuário
    private final CodigoRecuperacaoRepository codigoRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom random = new SecureRandom();

    public RecuperacaoSenhaService(UsuarioRepository usuarioRepository,
                                   CodigoRecuperacaoRepository codigoRepository,
                                   EmailService emailService,
                                   PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.codigoRepository = codigoRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void solicitarCodigoRecuperacao(SolicitarCodigoRequestDTO request) {
        String email = request.getEmail().toLowerCase().trim();

        // Verificar se o usuário existe
        if (!usuarioRepository.existsByEmail(email)) {
            // Por segurança, não revelamos se o email existe ou não
            log.warn("Tentativa de recuperação para email inexistente: {}", email);
            return;
        }
        // Invalidar códigos anteriores
        codigoRepository.marcarComoUtilizadosPorEmail(email);

        // Gerar novo código
        String codigo = gerarCodigoVerificacao();

        // Salvar código no banco
        CodigoRecuperacao codigoRecuperacao = new CodigoRecuperacao();
        codigoRecuperacao.setEmail(email);
        codigoRecuperacao.setCodigo(codigo);
        codigoRecuperacao.setUtilizado(false);

        codigoRepository.save(codigoRecuperacao);

        // Enviar email de forma assíncrona
        enviarEmailAsync(email, codigo);

        log.info("Código de recuperação gerado para: {}", email);
    }

    public boolean verificarCodigo(VerificarCodigoRequestDTO request) {
        String email = request.getEmail().toLowerCase().trim();
        String codigo = request.getCodigo().trim();

        Optional<CodigoRecuperacao> codigoRecuperacao =
                codigoRepository.findByEmailAndCodigoAndUtilizadoFalse(email, codigo);

        if (codigoRecuperacao.isEmpty()) {
            log.warn("Código inválido ou já utilizado para email: {}", email);
            return false;
        }

        if (codigoRecuperacao.get().isExpirado()) {
            log.warn("Código expirado para email: {}", email);
            return false;
        }

        log.info("Código verificado com sucesso para: {}", email);
        return true;
    }

    @Transactional
    public void atualizarSenhaDoEmail(AtualizarSenhaRequestDTO request) {
        String email = request.getEmail().toLowerCase().trim();
        String codigo = request.getCodigo().trim();
        String novaSenha = request.getNovaSenha();
        String confirmarSenha = request.getConfirmarSenha();

        // Validar se as senhas coincidem
        if (!novaSenha.equals(confirmarSenha)) {
            throw new IllegalArgumentException("As senhas não coincidem");
        }

        // Verificar código
        Optional<CodigoRecuperacao> codigoRecuperacao =
                codigoRepository.findByEmailAndCodigoAndUtilizadoFalse(email, codigo);

        if (codigoRecuperacao.isEmpty()) {
            throw new IllegalArgumentException("Código de verificação inválido");
        }

        if (codigoRecuperacao.get().isExpirado()) {
            throw new IllegalArgumentException("Código de verificação expirado");
        }

        // Buscar usuário (ajuste conforme sua entidade)
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Atualizar senha
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        // Marcar código como utilizado
        codigoRecuperacao.get().setUtilizado(true);
        codigoRepository.save(codigoRecuperacao.get());

        log.info("Senha atualizada com sucesso para: {}", email);
    }

    private String gerarCodigoVerificacao() {
        // Gera código de 6 dígitos
        return String.format("%06d", random.nextInt(1000000));
    }

    @Async
    public void enviarEmailAsync(String email, String codigo) {
        try {
            emailService.enviarCodigoRecuperacao(email, codigo);
        } catch (Exception e) {
            log.error("Erro ao enviar email de recuperação para {}: {}", email, e.getMessage());
        }
    }

    // Limpeza automática de códigos expirados (executa a cada hora)
    @Scheduled(fixedRate = 3600000) // 1 hora em milissegundos
    @Transactional
    public void limparCodigosExpirados() {
        codigoRepository.deleteByDataExpiracaoLessThan(LocalDateTime.now());
        log.info("Códigos expirados removidos do banco de dados");
    }
}
