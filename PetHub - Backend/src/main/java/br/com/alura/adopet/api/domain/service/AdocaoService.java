package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.api.dto.adocaoDTO.SolicitacaoAdocaoDTO;
import br.com.alura.adopet.api.domain.model.Adocao;
import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.domain.model.Tutor;
import br.com.alura.adopet.api.domain.model.validacoesRegrasAdocao.ValidadorDeAdocao;
import br.com.alura.adopet.api.domain.repository.AdocaoRepository;
import br.com.alura.adopet.api.domain.repository.PetRepository;
import br.com.alura.adopet.api.domain.repository.TutorRepository;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
public class AdocaoService {

    private final AdocaoRepository repository;
    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;
    private final EmailService emailService;
    private final ValidadorDeAdocao validadorDeAdocao; // Validador central de regras de adoção

    public AdocaoService(
            AdocaoRepository repository,
            PetRepository petRepository,
            TutorRepository tutorRepository,
            EmailService emailService,
            ValidadorDeAdocao validadorDeAdocao)
    {
        this.repository = repository;
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
        this.emailService = emailService;
        this.validadorDeAdocao = validadorDeAdocao;
    }

    // ------------------- Solicitação de adoção -------------------

    @Transactional // Operação de escrita no banco
    public void solicitar(SolicitacaoAdocaoDTO dto) {
        // Busca tutor e pet pelo ID, lança exceção se não existir
        Tutor tutor = tutorRepository.findById(dto.idTutor())
                .orElseThrow(() -> new ValidacaoException("Tutor não encontrado!"));
        Pet pet = petRepository.findById(dto.idPet())
                .orElseThrow(() -> new ValidacaoException("Pet não encontrado!"));

        // Executa todas as validações de adoção
        validadorDeAdocao.validar(dto);

        // Cria objeto Adocao com tutor, pet e motivo
        Adocao adocao = new Adocao(tutor, pet, dto.motivo());

        // Salva a adoção no banco
        repository.save(adocao);

        // Envia email para o abrigo avisando da nova solicitação
        emailService.enviarEmail(
                pet.getAbrigo().getEmail(),
                "Nova solicitação de adoção",
                "O tutor " + tutor.getNome() +
                        " solicitou a adoção do pet " + pet.getNome() +
                        " em " + adocao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
                        ". Favor avaliar a solicitação."
        );
    }

    // ------------------- Aprovação de adoção -------------------

    @Transactional
    public void aprovar(Long id) {
        // Busca a adoção pelo ID
        Adocao adocao = repository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Adoção não encontrada!"));

        // Marca a adoção como aprovada, mantendo a justificativa
        adocao.marcarComoAprovada(adocao.getMotivo());

        // Salva atualização no banco
        repository.save(adocao);

        // Envia email para o tutor informando aprovação
        emailService.enviarEmail(
                adocao.getTutor().getEmail(),
                "Adoção aprovada",
                "Parabéns " + adocao.getTutor().getNome() +
                        "! Sua adoção do pet " + adocao.getPet().getNome() +
                        " foi aprovada."
        );
    }

    // ------------------- Reprovação de adoção -------------------

    @Transactional
    public void reprovar(Long id, String justificativa) {
        // Busca a adoção pelo ID
        Adocao adocao = repository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Adoção não encontrada!"));

        // Marca a adoção como reprovada, adicionando justificativa
        adocao.marcarComoReprovada(justificativa);

        // Salva atualização no banco
        repository.save(adocao);

        // Envia email para o tutor informando a reprovação
        emailService.enviarEmail(
                adocao.getTutor().getEmail(),
                "Adoção reprovada",
                "Olá " + adocao.getTutor().getNome() +
                        ", sua adoção do pet " + adocao.getPet().getNome() +
                        " foi reprovada. Justificativa: " + justificativa
        );
    }
}
