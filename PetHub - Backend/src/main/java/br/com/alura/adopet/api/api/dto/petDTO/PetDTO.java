package br.com.alura.adopet.api.api.dto.petDTO;

import br.com.alura.adopet.api.domain.model.enums.TipoPet;

import java.math.BigDecimal;

public record PetDTO(Long id, String nome, String raca, Integer idade, TipoPet tipo, String cor,
                     BigDecimal peso) {
}
