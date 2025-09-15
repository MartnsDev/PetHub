package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.api.dto.adocaoDTO.SolicitacaoAdocaoDTO;
import br.com.alura.adopet.api.domain.model.Abrigo;
import br.com.alura.adopet.api.domain.model.Adocao;
import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.domain.model.Tutor;
import br.com.alura.adopet.api.domain.model.enums.StatusAdocao;
import br.com.alura.adopet.api.domain.model.enums.TipoPet;
import br.com.alura.adopet.api.domain.model.validacoesRegrasAdocao.ValidadorDeAdocao;
import br.com.alura.adopet.api.domain.repository.AdocaoRepository;
import br.com.alura.adopet.api.domain.repository.PetRepository;
import br.com.alura.adopet.api.domain.repository.TutorRepository;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdocaoServiceTest {

    private AdocaoRepository adocaoRepository;
    private PetRepository petRepository;
    private TutorRepository tutorRepository;
    private EmailService emailService;
    private ValidadorDeAdocao validadorDeAdocao;
    private AdocaoService adocaoService;

    @BeforeEach
    void setUp() {
        adocaoRepository = mock(AdocaoRepository.class);
        petRepository = mock(PetRepository.class);
        tutorRepository = mock(TutorRepository.class);
        emailService = mock(EmailService.class);
        validadorDeAdocao = mock(ValidadorDeAdocao.class);

        adocaoService = new AdocaoService(
                adocaoRepository,
                petRepository,
                tutorRepository,
                emailService,
                validadorDeAdocao
        );
    }

    @Test
    void deveSolicitarAdocaoComSucesso() {
        // Arrange
        Tutor tutor = Tutor.builder()
                .id(1L)
                .nome("João")
                .email("joao@email.com")
                .telefone("11999999999")
                .adocoes(new ArrayList<>())
                .build();

        Abrigo abrigo = new Abrigo(
                1L,
                "Abrigo SP",
                "1122334455",
                "abrigo@email.com",
                new ArrayList<>()
        );

        Pet pet = new Pet(
                1L,                    // id
                TipoPet.CACHORRO,      // tipo
                "Rex",                 // nome
                "SRD",                 // raca
                3,                     // idade
                "médio",               // cor
                new ArrayList<>(),     // imagens
                new BigDecimal("12.5"),// peso
                false,                 // adotado
                abrigo                 // abrigo
        );


        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        SolicitacaoAdocaoDTO dto = new SolicitacaoAdocaoDTO(1L, 1L, "Quero adotar para companhia");

        // Act
        adocaoService.solicitar(dto);

        // Assert
        verify(validadorDeAdocao).validar(dto);
        verify(adocaoRepository).save(any(Adocao.class));
        verify(emailService).enviarEmail(eq("abrigo@email.com"), anyString(), contains("João"));
    }

    @Test
    void deveLancarExcecaoQuandoTutorNaoEncontrado() {
        when(tutorRepository.findById(1L)).thenReturn(Optional.empty());

        SolicitacaoAdocaoDTO dto = new SolicitacaoAdocaoDTO(1L, 1L, "Motivo");

        assertThrows(ValidacaoException.class, () -> adocaoService.solicitar(dto));
        verify(adocaoRepository, never()).save(any());
    }

    @Test
    void deveAprovarAdocao() {
        Tutor tutor = Tutor.builder()
                .id(1L)
                .nome("Maria")
                .email("maria@email.com")
                .telefone("1133445566")
                .adocoes(new ArrayList<>())
                .build();

        Abrigo abrigo = new Abrigo(
                1L,
                "Abrigo SP",
                "1122334455",
                "abrigo@email.com",
                new ArrayList<>()
        );

        Pet pet = new Pet(
                1L,                    // id
                TipoPet.CACHORRO,      // tipo
                "Rex",                 // nome
                "SRD",                 // raca
                3,                     // idade
                "médio",               // cor
                new ArrayList<>(),     // imagens
                new BigDecimal("12.5"),// peso
                false,                 // adotado
                abrigo                 // abrigo
        );



        Adocao adocao = new Adocao(tutor, pet, "motivo");

        when(adocaoRepository.findById(1L)).thenReturn(Optional.of(adocao));

        adocaoService.aprovar(1L);

        assertEquals(StatusAdocao.APROVADO, adocao.getStatus());
        verify(adocaoRepository).save(adocao);
        verify(emailService).enviarEmail(eq(tutor.getEmail()), contains("aprovada"), contains("Luna"));
    }

    @Test
    void deveReprovarAdocao() {
        Tutor tutor = Tutor.builder()
                .id(1L)
                .nome("Carlos")
                .email("carlos@email.com")
                .telefone("11987654321")
                .adocoes(new ArrayList<>())
                .build();

        Abrigo abrigo = new Abrigo(
                1L,
                "Abrigo SP",
                "1122334455",
                "abrigo@email.com",
                new ArrayList<>()
        );

        Pet pet = new Pet(
                1L,                    // id
                TipoPet.CACHORRO,      // tipo
                "Rex",                 // nome
                "SRD",                 // raca
                3,                     // idade
                "médio",               // cor
                new ArrayList<>(),     // imagens
                new BigDecimal("12.5"),// peso
                false,                 // adotado
                abrigo                 // abrigo
        );


        Adocao adocao = new Adocao(tutor, pet, "motivo");

        when(adocaoRepository.findById(1L)).thenReturn(Optional.of(adocao));

        adocaoService.reprovar(1L, "Faltou documentos");

        assertEquals(StatusAdocao.REPROVADO, adocao.getStatus());
        verify(adocaoRepository).save(adocao);
        verify(emailService).enviarEmail(eq(tutor.getEmail()), contains("reprovada"), contains("Faltou documentos"));
    }
}
