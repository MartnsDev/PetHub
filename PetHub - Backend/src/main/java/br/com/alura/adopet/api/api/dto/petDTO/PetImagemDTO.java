package br.com.alura.adopet.api.api.dto.petDTO;

import org.springframework.web.multipart.MultipartFile;

public record PetImagemDTO(
        Long petId,
        MultipartFile foto
) {}
