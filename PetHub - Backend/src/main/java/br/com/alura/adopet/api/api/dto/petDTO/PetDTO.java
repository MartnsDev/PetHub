package br.com.alura.adopet.api.api.dto.petDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class PetDTO {

    private Long id;
    private String nome;
    private String raca;
    private Integer idade;
    private String cor;
    private BigDecimal peso;
    private String tipo;
    private Boolean adotado;
    private List<String> imagens;
    private String emailAbrigo;
}
