package io.github.chefiit.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import io.github.chefiit.model.Ingrediente;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IngredienteRepositoryTest {
   @Autowired
   private IngredienteRepository ingredienteRepository;

   @Test
   public void testPersistIngrediente() {
       Ingrediente ingrediente = new Ingrediente();
       ingrediente.setNome("Test");
       ingrediente.setUnidadeMedida("KG");

       Ingrediente saved = ingredienteRepository.save(ingrediente);
       assertNotNull(saved.getId());
   }
}
