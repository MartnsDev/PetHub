package br.com.alura.adopet.api.domain.service;

import br.com.alura.adopet.api.api.dto.abrigoDTO.AbrigoDTO;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarAbrigoDTO;
import br.com.alura.adopet.api.api.dto.abrigoDTO.CadastrarPetDTO;
import br.com.alura.adopet.api.api.dto.petDTO.PetDTO;
import br.com.alura.adopet.api.domain.model.Abrigo;
import br.com.alura.adopet.api.domain.model.Pet;
import br.com.alura.adopet.api.domain.repository.AbrigoRepository;
import br.com.alura.adopet.api.domain.repository.PetRepository;
import br.com.alura.adopet.api.infra.exceptions.ValidacaoException;
import br.com.alura.adopet.api.domain.model.enums.TipoPet;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbrigoServiceTest {

    @Mock
    private AbrigoRepository abrigoRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private AbrigoService abrigoService;

    private Abrigo abrigo;
    private Pet pet;

    @BeforeEach
    void setUp() {
        abrigo = Abrigo.builder()
                .id(1L)
                .nome("Abrigo Teste")
                .telefone("123456")
                .email("teste@abrigo.com")
                .build();

        pet = Pet.builder()
                .id(10L)
                .nome("Rex")
                .raca("Vira-lata")
                .idade(3)
                .tipo(TipoPet.CACHORRO)
                .cor("Preto")
                .peso(BigDecimal.valueOf(12.5))
                .abrigo(abrigo)
                .adotado(false)
                .build();

        abrigo.setPets(List.of(pet));
    }

    @Test
    void deveListarTodosOsAbrigos() {
        when(abrigoRepository.findAll()).thenReturn(List.of(abrigo));

        List<AbrigoDTO> resultado = abrigoService.listarTodosOsAbrigos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nome()).isEqualTo("Abrigo Teste");
        assertThat(resultado.get(0).pets()).hasSize(1);
    }

    @Test
    void deveCadastrarAbrigoComSucesso() {
        CadastrarAbrigoDTO dto = new CadastrarAbrigoDTO("Novo Abrigo", "111111", "novo@abrigo.com");

        when(abrigoRepository.existsByNomeOrTelefoneOrEmail(dto.nome(), dto.telefone(), dto.email()))
                .thenReturn(false);

        abrigoService.cadastrar(dto);

        verify(abrigoRepository, times(1)).save(any(Abrigo.class));
    }

    @Test
    void naoDeveCadastrarAbrigoDuplicado() {
        CadastrarAbrigoDTO dto = new CadastrarAbrigoDTO("Abrigo Teste", "123456", "teste@abrigo.com");

        when(abrigoRepository.existsByNomeOrTelefoneOrEmail(dto.nome(), dto.telefone(), dto.email()))
                .thenReturn(true);

        assertThatThrownBy(() -> abrigoService.cadastrar(dto))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Dados já cadastrados em outro abrigo");
    }

    @Test
    void deveListarPetsDeAbrigoPorId() {
        when(abrigoRepository.findById(1L)).thenReturn(Optional.of(abrigo));

        List<PetDTO> pets = abrigoService.listarPets("1");

        assertThat(pets).hasSize(1);
        assertThat(pets.get(0).getNome()).isEqualTo("Rex");
    }

    @Test
    void deveListarPetsDeAbrigoPorNome() {
        when(abrigoRepository.findByNome("Abrigo Teste")).thenReturn(Optional.of(abrigo));

        List<PetDTO> pets = abrigoService.listarPets("Abrigo Teste");

        assertThat(pets).hasSize(1);
        assertThat(pets.get(0).getNome()).isEqualTo("Rex");
    }

    @Test
    void deveLancarExcecaoQuandoAbrigoNaoEncontrado() {
        when(abrigoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> abrigoService.listarPets("99"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Abrigo não encontrado");
    }

    @Test
    void deveCadastrarPetNoAbrigo() {
        CadastrarPetDTO dto = new CadastrarPetDTO(
                "Rex",
                "SRD",
                3,
                TipoPet.CACHORRO,
                "médio",
                BigDecimal.valueOf(20),
                1L,                        // abrigoId
                List.of()                   // lista vazia de arquivos MultipartFile
        );

        when(abrigoRepository.findById(1L)).thenReturn(Optional.of(abrigo));

        abrigoService.cadastrarPet(1L, dto);

        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarPetEmAbrigoInexistente() {
        CadastrarPetDTO dto = new CadastrarPetDTO(
                "Rex",
                "SRD",
                3,
                TipoPet.CACHORRO,
                "médio",
                BigDecimal.valueOf(20),
                1L,                        // abrigoId
                List.of()                   // lista vazia de arquivos MultipartFile
        );
        when(abrigoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> abrigoService.cadastrarPet(99L, dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Abrigo não encontrado");
    }


    @Test
    void deveDeletarAbrigo() {
        when(abrigoRepository.existsById(1L)).thenReturn(true);

        abrigoService.deletar(1L);

        verify(abrigoRepository, times(1)).deleteById(1L);
    }

    @Test
    void naoDeveDeletarAbrigoInexistente() {
        when(abrigoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> abrigoService.deletar(99L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Abrigo não encontrado");
    }

    @Test
    void deveRetornarAbrigoPorId() {
        when(abrigoRepository.findById(1L)).thenReturn(Optional.of(abrigo));

        AbrigoDTO dto = abrigoService.getAbrigo(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.nome()).isEqualTo("Abrigo Teste");
    }

    @Test
    void deveLancarExcecaoAoBuscarAbrigoInexistente() {
        when(abrigoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> abrigoService.getAbrigo(99L))
                .isInstanceOf(ValidacaoException.class)
                .hasMessage("Abrigo não encontrado");
    }
}
