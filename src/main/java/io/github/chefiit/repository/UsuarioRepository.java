package io.github.chefiit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.chefiit.model.Receita;
import io.github.chefiit.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByNomeIgnoreCase(String nome);
    
    @Query("SELECT u FROM Usuario u WHERE u.tipoDieta = :tipoDieta")
    List<Usuario> findByTipoDieta(String tipoDieta);
    
    @Query("SELECT u FROM Usuario u WHERE u.objetivo = :objetivo")
    List<Usuario> findByObjetivo(String objetivo);
    
    @Query("""
        SELECT u FROM Usuario u 
        JOIN FETCH u.favoritas 
        WHERE u.id = :id
    """)
    Optional<Usuario> findByIdWithFavoritas(Long id);
    
    @Query("""
        SELECT COUNT(u) > 0 FROM Usuario u 
        JOIN u.favoritas r 
        WHERE u.id = :userId AND r.id = :receitaId
    """)
    boolean hasReceitaFavorita(Long userId, Long receitaId);

     @Query("""
        SELECT r FROM Receita r 
        JOIN r.favoritadaPor u 
        WHERE u.id = :usuarioId
    """)
    List<Receita> getFavoritas(Long usuarioId);
}