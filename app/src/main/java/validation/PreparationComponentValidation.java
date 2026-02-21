package validation;
import domain.PreparationComponent;


public class PreparationComponentValidation {
    public static void validate(PreparationComponent preparationComponent) throws IllegalArgumentException {
        if (preparationComponent.getQuantity() <0) {
            throw new IllegalArgumentException("Количество вещества не може быть отрицательным");
        }

    }
}
