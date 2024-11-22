package io.github.chefiit.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "ingredientes")
@Schema(description = "Representação de um ingrediente")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do ingrediente", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nome do ingrediente", example = "Farinha de Trigo")
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @Column(nullable = false)
    @Schema(description = "Unidade de medida do ingrediente", example = "KG", allowableValues = { "KG", "G", "L", "ML",
            "UNIDADE" })
    @NotNull(message = "Unidade de medida é obrigatória")
    private String unidadeMedida;

    // @ManyToOne
    // @JoinColumn(name = "receita_id")
    // private Receita receita;
}