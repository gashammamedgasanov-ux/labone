package validation;

import domain.entity.PreparationComponent;
import domain.enums.ComponentUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PreparationComponentValidationTest {

    @Test
    void validComponentDoesNotThrow() {
        PreparationComponent comp = new PreparationComponent(1L, 55L, 10.0, ComponentUnit.G);
        assertThatCode(() -> PreparationComponentValidation.validate(comp)).doesNotThrowAnyException();
    }

    @Test
    void quantityNegativeThrows() {
        PreparationComponent comp = new PreparationComponent(1L, 55L, -5.0, ComponentUnit.G);
        assertThatThrownBy(() -> PreparationComponentValidation.validate(comp))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не может быть отрицательным");
    }

    @Test
    void quantityZeroAllowed() {
        PreparationComponent comp = new PreparationComponent(1L, 55L, 0.0, ComponentUnit.G);
        assertThatCode(() -> PreparationComponentValidation.validate(comp)).doesNotThrowAnyException();
    }
}