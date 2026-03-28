package manager;

import domain.entity.Solution;
import domain.enums.SolutionConcentrationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolutionManagerTest {
    private SolutionManager manager;

    @BeforeEach//каждый тест с чистого менеджера
    void setUp() {
        manager = new SolutionManager();
    }

    @Test
    void addSolutionGeneratesIdAndStores() {
        Solution sol = manager.addSolution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        assertThat(sol.getId()).isPositive();
        assertThat(manager.getAll()).containsKey(sol.getId());
        assertThat(manager.getAll().get(sol.getId())).isEqualTo(sol);
    }

    @Test
    void addInvalidSolutionThrowsAndDoesNotStore() {
        assertThatThrownBy(() -> manager.addSolution(null, 0.9, SolutionConcentrationUnit.PERCENT, "water", "user"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(manager.getAll()).isEmpty();
    }
    //Можно сделать без лямбд
    //void addInvalidSolutionThrowsAndDoesNotStore() {
    //    try {
    //        manager.addSolution(null, 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
    //        fail("Expected IllegalArgumentException to be thrown");
    //    } catch (IllegalArgumentException e) {
    //        // Ожидаемое исключение: проверяем, что оно правильного типа
    //        assertTrue(e instanceof IllegalArgumentException);
    //        // Дополнительно можно проверить сообщение
    //        // assertTrue(e.getMessage().contains("Название раствора не может быть пустым"));
    //    }
    //    // Проверяем, что коллекция осталась пустой
    //    assertTrue(manager.getAll().isEmpty());
    //}

    @Test
    void getSolutionReturnsCorrect() {
        Solution sol = manager.addSolution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        Solution found = manager.getSolution(sol.getId());
        assertThat(found).isEqualTo(sol);
    }

    @Test
    void getSolutionNotFoundThrows() {
        assertThatThrownBy(() -> manager.getSolution(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void updateSolutionChangesFields() throws IllegalAccessException {
        Solution sol = manager.addSolution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        manager.updateSolution(sol.getId(), "KCl", 1.0, SolutionConcentrationUnit.G_PER_L, "ethanol");
        Solution updated = manager.getSolution(sol.getId());
        assertThat(updated.getName()).isEqualTo("KCl");
        assertThat(updated.getConcentration()).isEqualTo(1.0);
        assertThat(updated.getConcentrationUnit()).isEqualTo(SolutionConcentrationUnit.G_PER_L);
        assertThat(updated.getSolvent()).isEqualTo("ethanol");
    }

    @Test
    void updateWithInvalidDataThrowsAndDoesNotChange() {
        Solution sol = manager.addSolution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        assertThatThrownBy(() -> manager.updateSolution(sol.getId(), "", null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
        Solution unchanged = manager.getSolution(sol.getId());
        assertThat(unchanged.getName()).isEqualTo("NaCl");
    }

    @Test
    void removeSolutionRemovesIt() {
        Solution sol = manager.addSolution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        manager.removeSolution(sol.getId());
        assertThat(manager.getAll()).isEmpty();
        assertThatThrownBy(() -> manager.getSolution(sol.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void removeNonExistentThrows() {
        assertThatThrownBy(() -> manager.removeSolution(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void getAllReturnsUnmodifiableMap() {
        manager.addSolution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        var map = manager.getAll();
        assertThatThrownBy(() -> map.put(99L, null))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}