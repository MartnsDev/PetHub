package br.com.alura.adopet.api.service;

import br.com.alura.adopet.api.infra.ValidacaoException;
import br.com.alura.adopet.api.model.*;
import br.com.alura.adopet.api.model.dto.adocaoDTO.SolicitacaoAdocaoDTO;
import br.com.alura.adopet.api.repository.AdocaoRepository;
import br.com.alura.adopet.api.repository.PetRepository;
import br.com.alura.adopet.api.repository.TutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AdocaoService {

    private final AdocaoRepository repository;
    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;
    private final EmailService emailService;

    public AdocaoService(AdocaoRepository repository, PetRepository petRepository,
                         TutorRepository tutorRepository, EmailService emailService) {
        this.repository = repository;
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void solicitar(SolicitacaoAdocaoDTO dto) {
        // Buscar Tutor e Pet pelo ID
        Tutor tutor = tutorRepository.findById(dto.idTutor())
                .orElseThrow(() -> new ValidacaoException("Tutor não encontrado!"));
        Pet pet = petRepository.findById(dto.idPet())
                .orElseThrow(() -> new ValidacaoException("Pet não encontrado!"));

        if (Boolean.TRUE.equals(pet.getAdotado())) {
            throw new ValidacaoException("Pet já foi adotado!");
        }

        List<Adocao> adocoes = repository.findAll();

        boolean tutorAguardando = adocoes.stream()
                .anyMatch(a -> a.getTutor().equals(tutor) && a.getStatus() == StatusAdocao.AGUARDANDO_AVALIACAO);
        if (tutorAguardando) {
            throw new ValidacaoException("Tutor já possui outra adoção aguardando avaliação!");
        }

        boolean petAguardando = adocoes.stream()
                .anyMatch(a -> a.getPet().equals(pet) && a.getStatus() == StatusAdocao.AGUARDANDO_AVALIACAO);
        if (petAguardando) {
            throw new ValidacaoException("Pet já está aguardando avaliação para ser adotado!");
        }

        long totalAprovadas = adocoes.stream()
                .filter(a -> a.getTutor().equals(tutor) && a.getStatus() == StatusAdocao.APROVADO)
                .count();
        if (totalAprovadas >= 5) {
            throw new ValidacaoException("Tutor chegou ao limite máximo de 5 adoções!");
        }

        // Criar adoção usando builder
        Adocao adocao = Adocao.builder()
                .tutor(tutor)
                .pet(pet)
                .motivo(dto.motivo())
                .status(StatusAdocao.AGUARDANDO_AVALIACAO)
                .data(LocalDateTime.now())
                .build();

        repository.save(adocao);

        // Email para o abrigo avaliar a solicitação
        emailService.enviarEmail(
                pet.getAbrigo().getEmail(),
                "Nova solicitação de adoção",
                "O tutor " + tutor.getNome() +
                        " solicitou a adoção do pet " + pet.getNome() +
                        " em " + adocao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
                        ". Favor avaliar a solicitação."
        );
    }

    @Transactional
    public void aprovar(Long id) {
        Adocao adocao = repository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Adoção não encontrada!"));

        adocao.setStatus(StatusAdocao.APROVADO);
        // Usando o motivo como justificativa/observação
        adocao.setJustificativaStatus(adocao.getMotivo());
        repository.save(adocao);

        emailService.enviarEmail(
                adocao.getTutor().getEmail(),
                "Adoção aprovada",
                "Parabéns " + adocao.getTutor().getNome() +
                        "! Sua adoção do pet " + adocao.getPet().getNome() +
                        " foi aprovada. Motivo informado na solicitação: " + adocao.getMotivo()
        );
    }


    @Transactional
    public void reprovar(Long id, String justificativa) {
        Adocao adocao = repository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Adoção não encontrada!"));

        adocao.setStatus(StatusAdocao.REPROVADO);
        adocao.setJustificativaStatus(justificativa);
        repository.save(adocao);

        emailService.enviarEmail(
                adocao.getTutor().getEmail(),
                "Adoção reprovada",
                "Olá " + adocao.getTutor().getNome() +
                        "! Infelizmente sua adoção do pet " + adocao.getPet().getNome() +
                        ", solicitada em " + adocao.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
                        ", foi reprovada pelo abrigo " + adocao.getPet().getAbrigo().getNome() +
                        " com a seguinte justificativa: " + justificativa
        );
    }
}
