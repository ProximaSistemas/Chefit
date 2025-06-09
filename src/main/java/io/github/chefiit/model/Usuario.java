package io.github.chefiit.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@ToString(exclude = "favoritas")
@EqualsAndHashCode(exclude = "favoritas")
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    private int idade;
    private double peso;
    private double altura;
    
    @Column(length = 1)
    private char sexo;
    
    private String objetivo;
    private String tipoDieta;
    
    @Column(nullable = false)
    private LocalDateTime dataCadastro;

    // DADOS PARA LOGIN E AUTENTICAÇÃO
    @Enumerated(EnumType.STRING)
    @Column(name="tipo_usuario", nullable=false)
    private TipoUsuario tipoUsuario;
    
    @Column(unique = true)
    private String email;
    
    private String senha;
    
    @Column(unique = true)
    private String telefone;

    
    @ManyToMany
    @JoinTable(name = "usuarios_receitas_favoritas",
               joinColumns = @JoinColumn(name = "usuario_id"),
               inverseJoinColumns = @JoinColumn(name = "receita_id"))
    private Set<Receita> favoritas = new HashSet<>();

    public double calcularIMC() {
        return peso / (altura * altura);
    }

    public double calcularBMR() {
        // Fórmula de Harris-Benedict
        if (sexo == 'M') {
            return 88.362 + (13.397 * peso) + (4.799 * altura * 100) - (5.677 * idade);
        } else {
            return 447.593 + (9.247 * peso) + (3.098 * altura * 100) - (4.330 * idade);
        }
    }
}