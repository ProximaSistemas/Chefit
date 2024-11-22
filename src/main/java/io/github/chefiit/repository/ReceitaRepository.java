package io.github.chefiit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.chefiit.model.Receita;

@Repository
public interface ReceitaRepository extends JpaRepository<Receita, Long> {

    @Query("SELECT r FROM Receita r LEFT JOIN FETCH r.ingredientes WHERE r.id = :id")
    Optional<Receita> findByIdWithIngredientes(Long id);

    @Query("""
        SELECT DISTINCT r FROM Receita r 
        WHERE (:categoria IS NULL OR r.categoria = :categoria)
        AND (:tempoPreparoMaximo IS NULL OR r.tempoPreparo <= :tempoPreparoMaximo)
        AND (:caloriasMaximas IS NULL OR r.caloriasPorcao <= :caloriasMaximas)
        AND (:proteinasMinimas IS NULL OR r.proteinasPorcao >= :proteinasMinimas)
    """)
    List<Receita> findByFiltros(
        String categoria,
        Integer tempoPreparoMaximo,
        Integer caloriasMaximas,
        Double proteinasMinimas
    );
    
    @Query(nativeQuery = true, value = """
        SELECT DISTINCT r.* FROM receitas r
        WHERE (:tipoDieta = 'PROTEICA' AND r.proteinas_porcao >= 25
               OR :tipoDieta = 'LOW_CARB' AND r.carboidratos_porcao <= 30
               OR :tipoDieta IN ('MEDITERRANEA', 'VEGETARIANA'))
        AND r.calorias_porcao <= :caloriasMaximas
        ORDER BY r.calorias_porcao DESC
        LIMIT 3
    """)
    List<Receita> findRecomendadasByUsuario(String tipoDieta, double caloriasMaximas);

    @Query("""
                SELECT r FROM Receita r
                JOIN r.favoritadaPor u
                WHERE u.id = :usuarioId
            """)
    List<Receita> findFavoritasByUsuarioId(Long usuarioId);
}
