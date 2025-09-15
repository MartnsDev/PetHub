package br.com.alura.adopet.api.api.dto.abrigoDTO;

import br.com.alura.adopet.api.domain.model.enums.TipoPet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public record CadastrarPetDTO(

        @NotBlank(message = "Nome precisa ser preenchido")
        String nome,

        @NotBlank(message = "Raça precisa ser preenchida")
        String raca,

        @NotNull(message = "Idade precisa ser preenchida")
        Integer idade,

        @NotNull(message = "É GATO ou CACHORRO? Preencha o Tipo")
        TipoPet tipo,

        String cor,

        BigDecimal peso,

        @NotNull(message = "Selecione o id do abrigo em que deseja cadastrar o pet!")
        Long idAbrigo,

        // Fotos enviadas pelo formulário
        List<MultipartFile> fotos
) {}
