package io.github.chefiit.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "receitas")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "favoritadaPor")
@EqualsAndHashCode(exclude = "favoritadaPor")
public class Receita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @Column(nullable = false)
    private int tempoPreparo;

    @Column(nullable = false)
    private int porcoes;

    @Column(nullable = false, length = 2000)
    private String modoPreparo;

    private String categoria;
    private Double caloriasPorcao;
    private double proteinasPorcao;
    private double carboidratosPorcao;
    private double gordurasPorcao;

    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    @ManyToMany
    @JoinTable(name = "receitas_ingredientes", 
               joinColumns = @JoinColumn(name = "receita_id"), 
               inverseJoinColumns = @JoinColumn(name = "ingrediente_id"))
    private List<Ingrediente> ingredientes = new ArrayList<>();

    @ManyToMany(mappedBy = "favoritas")
    @JsonIgnore
    private Set<Usuario> favoritadaPor = new HashSet<>();
}