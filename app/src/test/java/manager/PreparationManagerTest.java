package manager;

import domain.entity.Preparation;
import domain.enums.FinalQuantityUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PreparationManagerTest {
    private PreparationManager manager;

    @BeforeEach
    void setUp() {
        manager = new PreparationManager();
    }

    @Test
    void addPreparationGeneratesIdAndStores() {
        Preparation prep = manager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "comment", "user", Instant.now());
        assertThat(prep.getId()).isPositive();
        assertThat(manager.getAllPreparations()).contains(prep);
    }

    @Test
    void addInvalidPreparationThrowsAndDoesNotStore() {
        assertThatThrownBy(() -> manager.addPreparation(1L, 0.0, FinalQuantityUnit.ML, "comment", "user", Instant.now()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(manager.getAllPreparations()).isEmpty();
    }

    @Test
    void getPreparationReturnsCorrect() {
        Preparation prep = manager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "comment", "user", Instant.now());
        Preparation found = manager.getPreparation(prep.getId());
        assertThat(found).isEqualTo(prep);
    }

    @Test
    void getPreparationNotFoundThrows() {
        assertThatThrownBy(() -> manager.getPreparation(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не найдено");
    }

    @Test
    void updatePreparationChangesFields() {
        Preparation prep = manager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "old", "user", Instant.now());
        manager.updatePreparation(prep.getId(), 200.0, FinalQuantityUnit.G, "new comment");
        Preparation updated = manager.getPreparation(prep.getId());
        assertThat(updated.getFinalQuantity()).isEqualTo(200.0);
        assertThat(updated.getFinalUnit()).isEqualTo(FinalQuantityUnit.G);
        assertThat(updated.getComment()).isEqualTo("new comment");
    }

    @Test
    void updatePreparationWithInvalidDataThrowsAndDoesNotChange() {
        Preparation prep = manager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "old", "user", Instant.now());
        assertThatThrownBy(() -> manager.updatePreparation(prep.getId(), -10.0, null, null))
                .isInstanceOf(IllegalArgumentException.class);
        Preparation unchanged = manager.getPreparation(prep.getId());
        assertThat(unchanged.getFinalQuantity()).isEqualTo(100.0);
    }

    @Test
    void removePreparationRemovesIt() {
        Preparation prep = manager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "comment", "user", Instant.now());
        manager.removePreparation(prep.getId());
        assertThat(manager.getAllPreparations()).isEmpty();
        assertThatThrownBy(() -> manager.getPreparation(prep.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getPreparationsForSolutionReturnsOnlyRelated() {
        manager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "c1", "user", Instant.now());
        manager.addPreparation(1L, 200.0, FinalQuantityUnit.ML, "c2", "user", Instant.now());
        manager.addPreparation(2L, 150.0, FinalQuantityUnit.ML, "c3", "user", Instant.now());

        List<Preparation> forSol1 = manager.getPreparationsForSolution(1L);
        assertThat(forSol1).hasSize(2);
        assertThat(forSol1).extracting(Preparation::getComment).containsExactlyInAnyOrder("c1", "c2");
    }

    @Test
    void getLastPreparationsForSolutionReturnsOrderedAndLimited() {
        Instant now = Instant.now();
        Preparation p1 = manager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "first", "user", now.minusSeconds(3600));
        Preparation p2 = manager.addPreparation(1L, 200.0, FinalQuantityUnit.ML, "second", "user", now);
        Preparation p3 = manager.addPreparation(1L, 150.0, FinalQuantityUnit.ML, "third", "user", now.minusSeconds(1800));

        List<Preparation> lastTwo = manager.getLastPreparationsForSolution(1L, 2);
        assertThat(lastTwo).containsExactly(p2, p3);
    }

    @Test
    void getLastPreparationsForSolutionWithLimitGreaterThanSizeReturnsAll() {
        manager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "c1", "user", Instant.now());
        List<Preparation> all = manager.getLastPreparationsForSolution(1L, 10);
        assertThat(all).hasSize(1);
    }

    @Test
    void existsReturnsTrueForExistingId() {
        Preparation prep = manager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "c", "user", Instant.now());
        assertThat(manager.exists(prep.getId())).isTrue();
        assertThat(manager.exists(999L)).isFalse();
    }
}