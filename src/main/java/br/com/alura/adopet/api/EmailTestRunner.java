//package br.com.alura.adopet.api;
//
//import br.com.alura.adopet.api.service.EmailService;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class EmailTestRunner implements CommandLineRunner {
//
//    private final EmailService emailService;
//
//    public EmailTestRunner(EmailService emailService) {
//        this.emailService = emailService;
//    }
//
//    @Override
//    public void run(String... args) {
//        emailService.enviarEmail(
//                "jake.timao@gmail.com",
//                "Teste de envio Spring Boot",
//                "Se você recebeu esse e-mail, a configuração funcionou 🚀" +
//                        "\nMatheus Martins Ribeiro é um Gênio!!"
//        );
//    }
//}
