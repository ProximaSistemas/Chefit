package io.github.chefiit.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.chefiit.model.Ingrediente;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    
    boolean existsByNomeIgnoreCase(String nome);
    
    Optional<Ingrediente> findByNomeIgnoreCase(String nome);
    
    @Query("SELECT i FROM Ingrediente i WHERE LOWER(i.nome) LIKE LOWER(CONCAT('%', :termo, '%'))")
    Page<Ingrediente> findByNomeContainingIgnoreCase(String termo, Pageable pageable);
    
    @Query("SELECT i FROM Ingrediente i WHERE i.unidadeMedida = :unidadeMedida")
    Page<Ingrediente> findByUnidadeMedida(String unidadeMedida, Pageable pageable);
}
