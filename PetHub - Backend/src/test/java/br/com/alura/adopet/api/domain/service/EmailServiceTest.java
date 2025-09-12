package br.com.alura.adopet.api.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
        // Configura o remetente manualmente, já que não vamos carregar o application.properties
        emailService.getClass().getDeclaredFields(); // necessário se fosse privado com @Value, mas podemos ajustar via reflection se quiser
    }

    @Test
    void deveEnviarEmailComSucesso() {
        // Arrange
        String destinatario = "teste@email.com";
        String assunto = "Assunto Teste";
        String mensagem = "Corpo do email";

        // Act
        emailService.enviarEmail(destinatario, assunto, mensagem);

        // Assert
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage emailEnviado = captor.getValue();
        assertEquals(destinatario, emailEnviado.getTo()[0]);
        assertEquals(assunto, emailEnviado.getSubject());
        assertEquals(mensagem, emailEnviado.getText());
    }
}
