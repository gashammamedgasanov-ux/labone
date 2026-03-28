package manager;

import domain.entity.Preparation;
import domain.entity.PreparationComponent;
import domain.enums.ComponentUnit;
import domain.enums.FinalQuantityUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;//интерфейс коллекции, который возвращают методы менеджера

//Статические импорты из AssertJ
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PreparationComponentManagerTest {
    private PreparationManager prepManager;
    private BatchService batchService;
    private PreparationComponentManager compManager;

    @BeforeEach
    void setUp() {
        prepManager = new PreparationManager();
        batchService = new BatchService();
        compManager = new PreparationComponentManager(prepManager, batchService);
    }

    @Test
    //метод не возвращает значения, проверяет успешное добавление компонента.
    //throws IllegalAccessException метод может выбросить это исключение (оно объявлено в сигнатуре addComponent менеджера). В тесте мы его не ловим, потому что ожидаем, что при корректных данных исключение не выбросится. Если оно всё же выбросится, тест упадёт.
    void addComponentWorks() throws IllegalAccessException {
        Preparation prep = prepManager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "comment", "user", Instant.now());
        PreparationComponent comp = compManager.addComponent(prep.getId(), 55L, 10.0, ComponentUnit.G);
        //начинает проверку ID компонента. assertThat статический метод AssertJ, он оборачивает переданное значение в специальный объект, который предоставляет методы проверок
        assertThat(comp.getId()).isPositive();
        assertThat(comp.getPreparationId()).isEqualTo(prep.getId());//Проверяет, что ID приготовления в компоненте совпадает с ID того приготовления, к которому мы его добавляли. Убеждаемся, что связь установлена правильно.
        assertThat(comp.getBatchId()).isEqualTo(55L);
        assertThat(comp.getQuantity()).isEqualTo(10.0);
        assertThat(comp.getUnit()).isEqualTo(ComponentUnit.G);
    }

    @Test
    void addComponentWithNonExistentPreparationThrows() {
        //лямбда-выражение, содержащее вызов addComponent с несуществующим ID приготовления (999L)
        assertThatThrownBy(() -> compManager.addComponent(999L, 55L, 10.0, ComponentUnit.G))
                .isInstanceOf(IllegalArgumentException.class)//проверяет, что выброшенное исключение IllegalArgumentException (или его подкласс). Если исключение другого типа или не выброшено, тест падает
                .hasMessageContaining("не существует");
    }

    @Test
    void addComponentWithInvalidBatchThrows() {
        Preparation prep = prepManager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "comment", "user", Instant.now());
        assertThatThrownBy(() -> compManager.addComponent(prep.getId(), 101L, 10.0, ComponentUnit.G))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не существует");
    }

    @Test
    void addComponentWithNegativeQuantityThrows() {
        Preparation prep = prepManager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "comment", "user", Instant.now());
        assertThatThrownBy(() -> compManager.addComponent(prep.getId(), 55L, -5.0, ComponentUnit.G))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не может быть отрицательным");
    }

    @Test
    void getComponentReturnsCorrect() throws IllegalAccessException {
        Preparation prep = prepManager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "comment", "user", Instant.now());
        PreparationComponent comp = compManager.addComponent(prep.getId(), 55L, 10.0, ComponentUnit.G);
        PreparationComponent found = compManager.getComponent(comp.getId());
        assertThat(found).isEqualTo(comp);
    }

    @Test
    void getComponentNotFoundThrows() {
        assertThatThrownBy(() -> compManager.getComponent(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void getComponentsForPreparationReturnsOnlyRelated() throws IllegalAccessException {
        Preparation prep1 = prepManager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "c1", "user", Instant.now());
        Preparation prep2 = prepManager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "c2", "user", Instant.now());
        PreparationComponent comp1 = compManager.addComponent(prep1.getId(), 55L, 10.0, ComponentUnit.G);
        PreparationComponent comp2 = compManager.addComponent(prep2.getId(), 55L, 20.0, ComponentUnit.G);

        List<PreparationComponent> forPrep1 = compManager.getComponentsForPreparation(prep1.getId());
        assertThat(forPrep1).containsExactly(comp1);
        assertThat(forPrep1).doesNotContain(comp2);
    }

    @Test
    void removeComponentRemovesIt() throws IllegalAccessException {
        Preparation prep = prepManager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "c", "user", Instant.now());
        PreparationComponent comp = compManager.addComponent(prep.getId(), 55L, 10.0, ComponentUnit.G);
        compManager.removeComponent(comp.getId());
        assertThat(compManager.getComponentsForPreparation(prep.getId())).isEmpty();
        assertThatThrownBy(() -> compManager.getComponent(comp.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void existsReturnsTrueForExistingId() throws IllegalAccessException {
        Preparation prep = prepManager.addPreparation(1L, 100.0, FinalQuantityUnit.ML, "c", "user", Instant.now());
        PreparationComponent comp = compManager.addComponent(prep.getId(), 55L, 10.0, ComponentUnit.G);
        assertThat(compManager.exists(comp.getId())).isTrue();
        assertThat(compManager.exists(999L)).isFalse();
    }
}