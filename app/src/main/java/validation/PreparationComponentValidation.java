package validation;
import domain.PreparationComponent;


public class PreparationComponentValidation {
    public static void validate(PreparationComponent preparationComponent) throws IllegalAccessException {
        if (preparationComponent.getQuantity() <0) {
            throw new IllegalAccessException("Количество вещества не може быть отрицательным");
        }

    }
}
