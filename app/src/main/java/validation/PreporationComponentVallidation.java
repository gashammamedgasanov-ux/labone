package validation;
import domain.PreporationComponent;


public class PreporationComponentVallidation {
    public static void vallidate(PreporationComponent preporationComponent) throws IllegalAccessException {
        if (preporationComponent.getQuantity() <0) {
            throw new IllegalAccessException("Количество вещества не може быть отрицательным");
        }

    }
}
