package br.com.alura.adopet.api.api.dto.recuperarSenhaDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SolicitarCodigoRequestDTO {
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;
}
