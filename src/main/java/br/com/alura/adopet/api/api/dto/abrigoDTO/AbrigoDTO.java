package br.com.alura.adopet.api.api.dto.abrigoDTO;

import br.com.alura.adopet.api.api.dto.petDTO.PetDTO;

import java.util.List;

public record AbrigoDTO(Long id, String nome, String telefone, String email, List<PetDTO> pets) {
}