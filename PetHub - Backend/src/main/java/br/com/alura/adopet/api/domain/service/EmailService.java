package br.com.alura.adopet.api.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j // Habilita logging para registrar ações e erros
public class EmailService {

    private final JavaMailSender emailSender; // Componente do Spring para envio de emails

    @Value("${spring.mail.username}") // Lê do application.properties o email remetente
    private String remetente;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    // ------------------- Envio de email genérico -------------------
    public void enviarEmail(String to, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage(); // Cria email simples
        email.setFrom(remetente); // Remetente configurado
        email.setTo(to);          // Destinatário
        email.setSubject(subject); // Assunto do email
        email.setText(message);    // Corpo do email
        emailSender.send(email);   // Envia email
    }

    // ------------------- Envio de código de recuperação -------------------
    public void enviarCodigoRecuperacao(String destinatario, String codigo) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage(); // Cria email
            mensagem.setFrom(remetente);                          // Remetente
            mensagem.setTo(destinatario);                         // Destinatário
            mensagem.setSubject("PetHub - Código de Recuperação de Senha"); // Assunto
            mensagem.setText(construirMensagemRecuperacao(codigo)); // Corpo do email

            emailSender.send(mensagem); // Envia email
            log.info("Email de recuperação enviado para: {}", destinatario); // Log de sucesso

        } catch (Exception e) {
            log.error("Erro ao enviar email para {}: {}", destinatario, e.getMessage()); // Log de erro
            throw new RuntimeException("Erro ao enviar email de recuperação"); // Lança exceção
        }
    }

    // Constrói a mensagem de recuperação com o código enviado
    private String construirMensagemRecuperacao(String codigo) {
        return String.format("""
            Olá!
            
            Você solicitou a recuperação da sua senha no PetHub.
            
            Seu código de verificação é: %s
            
            Este código expira em 15 minutos.
            
            Se você não solicitou esta recuperação, ignore este email.
            
            Atenciosamente,
            Equipe PetHub
            """, codigo);
    }
}
