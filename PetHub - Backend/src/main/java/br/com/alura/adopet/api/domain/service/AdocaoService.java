package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import br.com.alura.adopet.api.domain.model.Adocao;
import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.domain.model.Tutor;
import br.com.alura.adopet.api.api.dto.adocaoDTO.SolicitacaoAdocaoDTO;
import br.com.alura.adopet.api.domain.model.validacoesRegrasAdocao.ValidadorDeAdocao;
import br.com.alura.adopet.api.domain.repository.AdocaoRepository;
import br.com.alura.adopet.api.domain.repository.PetRepository;
import br.com.alura.adopet.api.domain.repository.TutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
public class AdocaoService {

    private final AdocaoRepository repository;
    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;
    private final EmailService emailService;
    private final ValidadorDeAdocao validadorDeAdocao; // <-- injetando o validador central

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

    @Transactional
    public void solicitar(SolicitacaoAdocaoDTO dto) {
        // Buscar Tutor e Pet pelo ID
        Tutor tutor = tutorRepository.findById(dto.idTutor())
                .orElseThrow(() -> new ValidacaoException("Tutor não encontrado!"));
        Pet pet = petRepository.findById(dto.idPet())
                .orElseThrow(() -> new ValidacaoException("Pet não encontrado!"));

        // Executa todas as validações
        validadorDeAdocao.validar(dto);
        // Criar adoção usando builder
        Adocao adocao = new Adocao(tutor, pet, dto.motivo());

        repository.save(adocao);

        // Enviar email
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

        adocao.marcarComoAprovada(adocao.getMotivo());
        repository.save(adocao);

        // Enviar email para o tutor
        emailService.enviarEmail(
                adocao.getTutor().getEmail(),
                "Adoção aprovada",
                "Parabéns " + adocao.getTutor().getNome() +
                        "! Sua adoção do pet " + adocao.getPet().getNome() +
                        " foi aprovada."
        );
    }

    @Transactional
    public void reprovar(Long id, String justificativa) {
        Adocao adocao = repository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Adoção não encontrada!"));

        adocao.marcarComoReprovada(justificativa);
        repository.save(adocao);

        // Enviar email para o tutor
        emailService.enviarEmail(
                adocao.getTutor().getEmail(),
                "Adoção reprovada",
                "Olá " + adocao.getTutor().getNome() +
                        ", sua adoção do pet " + adocao.getPet().getNome() +
                        " foi reprovada. Justificativa: " + justificativa
        );
    }
}
