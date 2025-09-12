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
//                "Este é um teste de envio de e-mail usando Spring Boot.\n" +
//                        "Se você está lendo isso, significa que tudo está funcionando perfeitamente!\n\n" +
//                        "Continue explorando, aprendendo e construindo coisas incríveis.\n\n" +
//                        "Abraços,\n" +
//                        "Matheus Martins"
//        );
//    }
//}
