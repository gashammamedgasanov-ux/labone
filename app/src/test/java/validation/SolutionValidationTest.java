package validation;

import domain.entity.Solution;
import domain.enums.SolutionConcentrationUnit;
import org.junit.jupiter.api.Test; //аннотация, чтобы было понятно JUnit

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolutionValidationTest {

    @Test
    void validSolutionDoesNotThrow() {
        Solution solution = new Solution("NaCl 0.9%", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");//создаём объект класса Solution с допустимыми значениями
        assertThatCode(() -> SolutionValidation.validate(solution))//assertThatCode специальный метод из библиотеки AssertJ, который запускает код из фигурных скобок и потом запоминает, выбросилось исключение или не выбросилось; () -> SolutionValidation.validate(solution)) - вот это конструкция лямбда, в скобках параметры(у нас нет), стрелка отделяет параметр от тела, само тело это вызов метода validate у класса SolutionValidation, куда мы передаём созданный объект solution
                .doesNotThrowAnyException();//проверка, говорим, что код который передали, не выбросил ни одного исключения, если код выбросит исключение, то тест упадёт
    }

    @Test
    void nameNullThrows() {
        Solution solution = new Solution(null, 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        assertThatThrownBy(() -> SolutionValidation.validate(solution))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Название раствора не может быть пустым");
    }

    @Test
    void nameEmptyStringThrows() {
        Solution solution = new Solution("", 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        assertThatThrownBy(() -> SolutionValidation.validate(solution))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Название раствора не может быть пустым");
    }

    @Test
    void nameTooLongThrows() {
        String longName = "a".repeat(129);
        Solution solution = new Solution(longName, 0.9, SolutionConcentrationUnit.PERCENT, "water", "user");
        assertThatThrownBy(() -> SolutionValidation.validate(solution))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Название раствора слишком длинное");
    }

    @Test
    void concentrationNegativeThrows() {
        Solution solution = new Solution("NaCl", -0.1, SolutionConcentrationUnit.PERCENT, "water", "user");
        assertThatThrownBy(() -> SolutionValidation.validate(solution))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Концентрация не должна быть отрицательной");
    }

    @Test
    void solventTooLongThrows() {
        String longSolvent = "a".repeat(65);
        Solution solution = new Solution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, longSolvent, "user");
        assertThatThrownBy(() -> SolutionValidation.validate(solution))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("слишком длинное");
    }

    @Test
    void nullSolventAllowed() {
        Solution solution = new Solution("NaCl", 0.9, SolutionConcentrationUnit.PERCENT, null, "user");
        assertThatCode(() -> SolutionValidation.validate(solution)).doesNotThrowAnyException();
    }
}