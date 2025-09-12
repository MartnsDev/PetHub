package br.com.alura.adopet.api.api.dto.abrigoDTO;

import br.com.alura.adopet.api.domain.model.enums.TipoPet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CadastrarPetDTO(
        @NotBlank(message = "Nome precisa ser Preenchido")
        String nome,
        @NotBlank(message = "Raça precisa ser preenchida")
        String raca,
        @NotNull(message = "Idade Precisa ser Preenchida")
        Integer idade,
        @NotNull(message = "É GATO ou CACHORRO? Preencha o Tipo")
        TipoPet tipo,// <--- aqui tem que ser @NotNull
        String cor,
        BigDecimal peso,
        @NotNull(message = "Selecione o id do abrigo em que deseja cadastrar o pet!")
        Long idAbrigo
) {}

