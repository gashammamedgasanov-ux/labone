package validation;

import domain.entity.Preparation;
import domain.enums.FinalQuantityUnit;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PreparationValidatorTest {

    @Test
    void validPreparationDoesNotThrow() {
        Preparation prep = new Preparation(1L, 100.0, FinalQuantityUnit.ML, "comment", "user", Instant.now());
        assertThatCode(() -> PreparationValidator.validate(prep)).doesNotThrowAnyException();
    }

    @Test
    void finalQuantityZeroThrows() {
        Preparation prep = new Preparation(1L, 0.0, FinalQuantityUnit.ML, "comment", "user", Instant.now());
        assertThatThrownBy(() -> PreparationValidator.validate(prep))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("больше нуля");
    }

    @Test
    void finalQuantityNegativeThrows() {
        Preparation prep = new Preparation(1L, -10.0, FinalQuantityUnit.ML, "comment", "user", Instant.now());
        assertThatThrownBy(() -> PreparationValidator.validate(prep))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("больше нуля");
    }

    @Test
    void nullFinalUnitThrows() {
        Preparation prep = new Preparation(1L, 100.0, null, "comment", "user", Instant.now());
        assertThatThrownBy(() -> PreparationValidator.validate(prep))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Единицы измерения не могут быть пустыми");
    }

    @Test
    void commentTooLongThrows() {
        String longComment = "a".repeat(129);
        Preparation prep = new Preparation(1L, 100.0, FinalQuantityUnit.ML, longComment, "user", Instant.now());
        assertThatThrownBy(() -> PreparationValidator.validate(prep))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Комментарий слишком длинный");
    }

    @Test
    void nullCommentAllowed() {
        Preparation prep = new Preparation(1L, 100.0, FinalQuantityUnit.ML, null, "user", Instant.now());
        assertThatCode(() -> PreparationValidator.validate(prep)).doesNotThrowAnyException();
    }
}