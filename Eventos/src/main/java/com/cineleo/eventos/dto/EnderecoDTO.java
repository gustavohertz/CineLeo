package com.cineleo.eventos.dto;

import com.cineleo.eventos.entity.Endereco;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnderecoDTO {

    @NotBlank(message = "Logradouro é obrigatório")
    @Size(max = 200)
    private String logradouro;

    @NotBlank(message = "Número é obrigatório")
    @Size(max = 20)
    private String numero;

    @Size(max = 100)
    private String complemento;

    @NotBlank(message = "Bairro é obrigatório")
    @Size(max = 100)
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 100)
    private String cidade;

    @NotBlank(message = "UF é obrigatória")
    @Size(min = 2, max = 2, message = "UF deve ter 2 caracteres")
    private String uf;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido")
    private String cep;

    public static EnderecoDTO from(Endereco endereco) {
        if (endereco == null) return null;
        return EnderecoDTO.builder()
                .logradouro(endereco.getLogradouro())
                .numero(endereco.getNumero())
                .complemento(endereco.getComplemento())
                .bairro(endereco.getBairro())
                .cidade(endereco.getCidade())
                .uf(endereco.getUf())
                .cep(endereco.getCep())
                .build();
    }

    public Endereco toEntity() {
        return Endereco.builder()
                .logradouro(this.logradouro)
                .numero(this.numero)
                .complemento(this.complemento)
                .bairro(this.bairro)
                .cidade(this.cidade)
                .uf(this.uf)
                .cep(this.cep)
                .build();
    }
}
